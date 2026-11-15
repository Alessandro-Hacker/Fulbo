// Registro: crea el perfil del usuario despues de verificar que es mayor de 16.
// Flujo 2 del documento de diagramas.
//
// NO se pide DNI. La Ley 29733 (principio de calidad, art. 8) exige recoger
// solo los datos necesarios para la finalidad, y para armar pichangas el DNI
// no lo es: obliga a tratarlo como dato sensible en logs, backups y caches, y
// espanta usuarios en el registro. La identidad real de un jugador la respalda
// su reputacion, no su documento.
import { onCall, HttpsError } from "firebase-functions/v2/https";
import {
  FieldValue, NIVELES, POSICIONES, exigirSesion, refUsuario, texto,
} from "./comun";

export const EDAD_MINIMA = 16;

interface DatosPerfil {
  nombre: string;
  apellido: string;
  alias: string;
  fechaNacimiento: string; // "YYYY-MM-DD"
  ciudad: string;
  posicion: string;
  nivel: string;
  aceptaTerminos: boolean;
}

function edadEnAnios(fecha: string): number {
  const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(fecha);
  if (!m) throw new HttpsError("invalid-argument", "Fecha de nacimiento invalida.");
  const nac = new Date(Date.UTC(+m[1], +m[2] - 1, +m[3]));
  const hoy = new Date();
  let edad = hoy.getUTCFullYear() - nac.getUTCFullYear();
  const aunNoCumple =
    hoy.getUTCMonth() < nac.getUTCMonth() ||
    (hoy.getUTCMonth() === nac.getUTCMonth() && hoy.getUTCDate() < nac.getUTCDate());
  if (aunNoCumple) edad--;
  return edad;
}

export const completarPerfil = onCall(async (req) => {
  const uid = exigirSesion(req);
  const d = req.data as DatosPerfil;

  if (d.aceptaTerminos !== true) {
    throw new HttpsError("failed-precondition", "Debes aceptar los terminos.");
  }
  if (edadEnAnios(d.fechaNacimiento) < EDAD_MINIMA) {
    throw new HttpsError("failed-precondition", `Debes tener ${EDAD_MINIMA} anos o mas.`);
  }
  if (!POSICIONES.includes(d.posicion as never)) {
    throw new HttpsError("invalid-argument", "Posicion invalida.");
  }
  if (!NIVELES.includes(d.nivel as never)) {
    throw new HttpsError("invalid-argument", "Nivel invalido.");
  }

  const refPerfil = refUsuario(uid);

  // Una cuenta de Firebase Auth = un perfil. La transaccion evita que dos
  // llamadas simultaneas desde el mismo usuario creen el perfil dos veces.
  await refPerfil.firestore.runTransaction(async (tx) => {
    const perfilSnap = await tx.get(refPerfil);
    if (perfilSnap.exists) throw new HttpsError("already-exists", "Tu perfil ya existe.");

    // Perfil publico
    tx.create(refPerfil, {
      nombre: texto(d.nombre, "nombre"),
      apellido: texto(d.apellido, "apellido"),
      alias: texto(d.alias, "alias", 30),
      ciudad: texto(d.ciudad, "ciudad"),
      posicion: d.posicion,
      nivel: d.nivel,
      ubicacion: null,
      foto_url: null,
      // Reputacion: arranca SIN calificaciones (null), no en 0 ni en 5.
      rep_global: null,
      rep_puntualidad: null,
      rep_asistencia: null,
      rep_pago: null,
      rep_trato: null,
      num_calificaciones: 0,
      partidos_jugados: 0,
      faltas: 0,
      plan: "gratuito",
      plan_vence_en: null,
      creado_en: FieldValue.serverTimestamp(),
    });

    // Datos personales: solo los lee el dueno (ver reglas). Guardamos la
    // fecha de nacimiento, no la edad, porque la edad cambia sola; y el
    // momento en que acepto los terminos, que la ley exige poder acreditar.
    tx.create(refPerfil.collection("privado").doc("datos"), {
      fecha_nacimiento: d.fechaNacimiento,
      acepto_terminos_en: FieldValue.serverTimestamp(),
    });
  });

  return { ok: true };
});
