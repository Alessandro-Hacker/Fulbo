import { initializeTestEnvironment, assertSucceeds, assertFails } from "@firebase/rules-unit-testing";
import { readFileSync } from "node:fs";
import { doc, setDoc, updateDoc, deleteDoc, getDoc, Timestamp, GeoPoint, serverTimestamp, setLogLevel } from "firebase/firestore";

// Cada prueba con assertFails provoca un PERMISSION_DENIED a proposito y el
// SDK lo escribe en rojo. Sin esto la salida son 20 trazas de error que
// parecen fallas y no lo son.
setLogLevel("silent");

// De donde sale el puerto, en orden: la variable que exporta emulators:exec
// y, si no esta, firebase.json. Nunca escrito a mano aqui: si alguien mueve
// el puerto (el 8080 suele estar ocupado por XAMPP, Tomcat o Docker) basta
// con cambiarlo en firebase.json.
const config = JSON.parse(readFileSync(new URL("../firebase.json", import.meta.url), "utf8"));
const porDefecto = `127.0.0.1:${config.emulators?.firestore?.port ?? 8080}`;
const [emuHost, emuPort] = (process.env.FIRESTORE_EMULATOR_HOST || porDefecto).split(":");
console.log(`Emulador de Firestore en ${emuHost}:${emuPort}`);

const env = await initializeTestEnvironment({
  projectId: "fulbo-test",
  firestore: { rules: readFileSync(new URL("../firestore.rules", import.meta.url), "utf8"), host: emuHost, port: Number(emuPort) },
}).catch((e) => {
  // Si en el puerto responde otra cosa (un Apache, por ejemplo) el mensaje
  // es una pagina HTML y no se entiende nada. Mejor decirlo claro.
  console.error(`\nNo se pudo hablar con el emulador en ${emuHost}:${emuPort}.`);
  console.error("Revisa que no haya otro programa en ese puerto y que firebase.json apunte al correcto.\n");
  throw e;
});
await env.withSecurityRulesDisabled(async (ctx) => {
  const db = ctx.firestore();
  for (const u of ["ana", "beto", "caro"]) await setDoc(doc(db, `usuarios/${u}`), { alias: u, plan: "gratuito", rep_global: null });
  await setDoc(doc(db, "usuarios/ana/privado/datos"), { fecha_nacimiento: "2000-05-14" });
  await setDoc(doc(db, "partidos/p1"), { organizador_uid: "ana", estado: "en_calificacion" });
  await setDoc(doc(db, "partidos/p1/inscripciones/ana"), { asistio: true });
  await setDoc(doc(db, "partidos/p1/inscripciones/beto"), { asistio: true });
  await setDoc(doc(db, "partidos/p1/inscripciones/caro"), { asistio: false });
  // ana y caro son duenos VERIFICADOS; beto se registro pero nadie lo aprobo.
  await setDoc(doc(db, "duenos_cancha/ana"), { razon_social: "Canchas Ana SAC", ruc: "20123456789", telefono: "987654321", verificado: true });
  await setDoc(doc(db, "duenos_cancha/caro"), { razon_social: "Losas Caro EIRL", ruc: "20555666777", telefono: "987000111", verificado: true });
  await setDoc(doc(db, "duenos_cancha/beto"), { razon_social: "Grass Beto SAC", ruc: "20111222333", telefono: "987333444", verificado: false });
  await setDoc(doc(db, "canchas/c1"), { dueno_uid: "ana", nombre: "La Bombonerita", direccion: "Av. Larco 123", distrito: "Miraflores",
    ciudad: "Lima", ubicacion: new GeoPoint(-12.12, -77.03), superficie: "sintetico", formatos: ["5v5", "7v7"], techada: true,
    precio_hora: 120, abre_min: 420, cierra_min: 1380, telefono: "987654321", activa: true, creado_en: serverTimestamp(),
    geohash: "6mc87p2k", rating: null, num_resenas: 0 });
});
const ver = (uid) => env.authenticatedContext(uid, { email_verified: true }).firestore();
const sinVerif = env.authenticatedContext("dani", { email_verified: true }).firestore(); // sin perfil
const manana = Timestamp.fromMillis(Date.now() + 86400000);
const partido = (o = {}) => ({ organizador_uid: "ana", cancha_id: null, cancha_nombre: "La Bombonerita", fecha_hora: manana,
  formato: "7v7", cupos_totales: 14, cupos_libres: 13, nivel_buscado: "casual", rep_minima: null, costo_persona: 10,
  cancha_confirmada: true, ubicacion: new GeoPoint(-12.12, -77.03), ciudad: "Lima", estado: "publicado", creado_en: serverTimestamp(), ...o });

let pasaron = 0, fallaron = 0;
const t = async (nombre, p) => {
  try { await p; pasaron++; console.log("OK   ", nombre); }
  catch (e) { fallaron++; console.log("FALLA", nombre, "->", e.message); process.exitCode = 1; }
};
await t("crear partido valido", assertSucceeds(setDoc(doc(ver("ana"), "partidos/n1"), partido())));
await t("sin perfil no crea", assertFails(setDoc(doc(sinVerif, "partidos/n2"), partido({ organizador_uid: "dani" }))));
await t("cupos mal contados", assertFails(setDoc(doc(ver("ana"), "partidos/n3"), partido({ cupos_libres: 14 }))));
await t("fecha pasada", assertFails(setDoc(doc(ver("ana"), "partidos/n4"), partido({ fecha_hora: Timestamp.fromMillis(Date.now() - 1000) }))));
await t("no puede inventar organizador_rep", assertFails(setDoc(doc(ver("ana"), "partidos/n5"), partido({ organizador_rep: 5 }))));
await t("no se sube la reputacion", assertFails(updateDoc(doc(ver("ana"), "usuarios/ana"), { rep_global: 5 })));
await t("si cambia su alias", assertSucceeds(updateDoc(doc(ver("ana"), "usuarios/ana"), { alias: "anita" })));
await t("otro no lee privado", assertFails(getDoc(doc(ver("beto"), "usuarios/ana/privado/datos"))));
await t("dueno lee privado", assertSucceeds(getDoc(doc(ver("ana"), "usuarios/ana/privado/datos"))));
await t("no se inscribe directo", assertFails(setDoc(doc(ver("beto"), "partidos/p1/inscripciones/beto"), { estado: "aceptado" })));
const cal = (o = {}) => ({ emisor_uid: "ana", receptor_uid: "beto", puntualidad: 5, asistencia: 4, pago: 5, trato: 5, comentario: "Buen pase", creado_en: serverTimestamp(), ...o });
await t("calificacion valida", assertSucceeds(setDoc(doc(ver("ana"), "partidos/p1/calificaciones/ana_beto"), cal())));
await t("id no coincide", assertFails(setDoc(doc(ver("ana"), "partidos/p1/calificaciones/x"), cal())));
await t("califica a quien no asistio", assertFails(setDoc(doc(ver("ana"), "partidos/p1/calificaciones/ana_caro"), cal({ receptor_uid: "caro" }))));
await t("nota fuera de rango", assertFails(setDoc(doc(ver("beto"), "partidos/p1/calificaciones/beto_ana"), cal({ emisor_uid: "beto", receptor_uid: "ana", trato: 7 }))));
await t("receptor no lee la nota", assertFails(getDoc(doc(ver("beto"), "partidos/p1/calificaciones/ana_beto"))));
await t("equipo solo premium", assertFails(setDoc(doc(ver("ana"), "equipos/e1"), { nombre: "Los Pibes", ciudad: "Lima", formato: "7v7", creador_uid: "ana", creado_en: 1 })));

// ---------- duenos de cancha y canchas ----------
const dueno = (o = {}) => ({ razon_social: "Canchas Dani SAC", ruc: "20444555666", telefono: "987222333", verificado: false, ...o });
await t("registro de dueno valido", assertSucceeds(setDoc(doc(ver("dani"), "duenos_cancha/dani"), dueno())));
await t("dueno no nace verificado", assertFails(setDoc(doc(ver("dani"), "duenos_cancha/dani2"), dueno({ verificado: true }))));
await t("ruc de 11 digitos", assertFails(setDoc(doc(ver("dani"), "duenos_cancha/dani3"), dueno({ ruc: "123" }))));
await t("no se registra a nombre de otro", assertFails(setDoc(doc(ver("dani"), "duenos_cancha/beto2"), dueno())));
await t("dueno no se auto-verifica", assertFails(updateDoc(doc(ver("beto"), "duenos_cancha/beto"), { verificado: true })));
await t("dueno si cambia su telefono", assertSucceeds(updateDoc(doc(ver("ana"), "duenos_cancha/ana"), { telefono: "987000000" })));

const cancha = (o = {}) => ({ dueno_uid: "ana", nombre: "La Bombonerita", direccion: "Av. Larco 123", distrito: "Miraflores", ciudad: "Lima",
  ubicacion: new GeoPoint(-12.12, -77.03), superficie: "sintetico", formatos: ["5v5", "7v7"], techada: true, precio_hora: 120,
  abre_min: 420, cierra_min: 1380, telefono: "987654321", activa: true, creado_en: serverTimestamp(), ...o });
await t("dueno verificado crea cancha", assertSucceeds(setDoc(doc(ver("ana"), "canchas/k1"), cancha())));
await t("dueno sin verificar no crea", assertFails(setDoc(doc(ver("beto"), "canchas/k2"), cancha({ dueno_uid: "beto" }))));
await t("quien no es dueno no crea", assertFails(setDoc(doc(ver("dani"), "canchas/k3"), cancha({ dueno_uid: "dani" }))));
await t("no crea cancha a nombre de otro", assertFails(setDoc(doc(ver("ana"), "canchas/k4"), cancha({ dueno_uid: "caro" }))));
await t("no puede inventar rating", assertFails(setDoc(doc(ver("ana"), "canchas/k5"), cancha({ rating: 5 }))));
await t("no puede inventar geohash", assertFails(setDoc(doc(ver("ana"), "canchas/k6"), cancha({ geohash: "zzzzzzzz" }))));
await t("superficie invalida", assertFails(setDoc(doc(ver("ana"), "canchas/k7"), cancha({ superficie: "parquet" }))));
await t("formato invalido", assertFails(setDoc(doc(ver("ana"), "canchas/k8"), cancha({ formatos: ["9v9"] }))));
await t("precio negativo", assertFails(setDoc(doc(ver("ana"), "canchas/k9"), cancha({ precio_hora: -10 }))));
await t("horario fuera de rango", assertFails(setDoc(doc(ver("ana"), "canchas/k10"), cancha({ cierra_min: 3000 }))));
await t("dueno edita su precio", assertSucceeds(updateDoc(doc(ver("ana"), "canchas/c1"), { precio_hora: 130 })));
await t("dueno la desactiva", assertSucceeds(updateDoc(doc(ver("ana"), "canchas/c1"), { activa: false })));
await t("otro dueno no edita mi cancha", assertFails(updateDoc(doc(ver("caro"), "canchas/c1"), { precio_hora: 1 })));
await t("no se sube el rating de la cancha", assertFails(updateDoc(doc(ver("ana"), "canchas/c1"), { rating: 5 })));
await t("no se mueve el geohash a mano", assertFails(updateDoc(doc(ver("ana"), "canchas/c1"), { geohash: "zzzzzzzz" })));
await t("nadie borra una cancha", assertFails(deleteDoc(doc(ver("ana"), "canchas/c1"))));

console.log(`\n${pasaron + fallaron} pruebas: ${pasaron} OK, ${fallaron} fallas`);
await env.cleanup();
