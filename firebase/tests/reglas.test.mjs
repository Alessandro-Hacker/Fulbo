import { initializeTestEnvironment, assertSucceeds, assertFails } from "@firebase/rules-unit-testing";
import { readFileSync } from "node:fs";
import { doc, setDoc, updateDoc, getDoc, Timestamp, GeoPoint, serverTimestamp } from "firebase/firestore";

const env = await initializeTestEnvironment({
  projectId: "fulbo-test",
  firestore: { rules: readFileSync(new URL("../firestore.rules", import.meta.url), "utf8"), host: "127.0.0.1", port: 8080 },
});
await env.withSecurityRulesDisabled(async (ctx) => {
  const db = ctx.firestore();
  for (const u of ["ana", "beto", "caro"]) await setDoc(doc(db, `usuarios/${u}`), { alias: u, plan: "gratuito", rep_global: null });
  await setDoc(doc(db, "usuarios/ana/privado/datos"), { dni_hash: "x" });
  await setDoc(doc(db, "partidos/p1"), { organizador_uid: "ana", estado: "en_calificacion" });
  await setDoc(doc(db, "partidos/p1/inscripciones/ana"), { asistio: true });
  await setDoc(doc(db, "partidos/p1/inscripciones/beto"), { asistio: true });
  await setDoc(doc(db, "partidos/p1/inscripciones/caro"), { asistio: false });
});
const ver = (uid) => env.authenticatedContext(uid, { email_verified: true }).firestore();
const sinVerif = env.authenticatedContext("dani", { email_verified: true }).firestore(); // sin perfil
const manana = Timestamp.fromMillis(Date.now() + 86400000);
const partido = (o = {}) => ({ organizador_uid: "ana", cancha_id: null, cancha_nombre: "La Bombonerita", fecha_hora: manana,
  formato: "7v7", cupos_totales: 14, cupos_libres: 13, nivel_buscado: "casual", rep_minima: null, costo_persona: 10,
  cancha_confirmada: true, ubicacion: new GeoPoint(-12.12, -77.03), ciudad: "Lima", estado: "publicado", creado_en: serverTimestamp(), ...o });

const t = async (nombre, p) => { try { await p; console.log("OK  ", nombre); } catch (e) { console.log("FALLA", nombre, e.message); process.exitCode = 1; } };
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
await env.cleanup();
