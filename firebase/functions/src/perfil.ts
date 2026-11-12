// Registro: crea el perfil del usuario despues de validar DNI y edad.
// Flujo 2 del documento de diagramas.
import { createHmac } from "node:crypto";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import {
  db, FieldValue, NIVELES, POSICIONES, exigirSesion, refUsuario, texto,
} from "./comun";

// Clave secreta para el hash del DNI. Se crea una sola vez con:
//   firebase functions:secrets:set DNI_PEPPER
// Sin esta clave, un hash simple de 8 digitos se revierte probando los
// 100 millones de combinaciones en minutos.
const DNI_PEPPER = defineSecret("DNI_PEPPER");

interface DatosPerfil {
  nombre: string;
  apellido: string;
  alias: string;
  dni: string;
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

export const completarPerfil = onCall({ secrets: [DNI_PEPPER] }, async (req) => {
  const uid = exigirSesion(req);
  const d = req.data as DatosPerfil;

  if (d.aceptaTerminos !== true) {
    throw new HttpsError("failed-precondition", "Debes aceptar los terminos.");
  }
  if (typeof d.dni !== "string" || !/^\d{8}$/.test(d.dni)) {
    throw new HttpsError("invalid-argument", "El DNI debe tener 8 digitos.");
  }
  if (edadEnAnios(d.fechaNacimiento) < 16) {
    throw new HttpsError("failed-precondition", "Debes tener 16 anos o mas.");
  }
  if (!POSICIONES.includes(d.posicion as never)) {
    throw new HttpsError("invalid-argument", "Posicion invalida.");
  }
  if (!NIVELES.includes(d.nivel as never)) {
    throw new HttpsError("invalid-argument", "Nivel invalido.");
  }

  const dniHash = createHmac("sha256", DNI_PEPPER.value()).update(d.dni).digest("hex");
  const refDni = db.doc(`dni_registrados/${dniHash}`);
  const refPerfil = refUsuario(uid);

  await db.runTransaction(async (tx) => {
    const [dniSnap, perfilSnap] = await Promise.all([tx.get(refDni), tx.get(refPerfil)]);
    if (perfilSnap.exists) throw new HttpsError("already-exists", "Tu perfil ya existe.");
    if (dniSnap.exists) throw new HttpsError("already-exists", "Ese DNI ya esta registrado.");

    tx.create(refDni, { uid, creado_en: FieldValue.serverTimestamp() });

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

    // Datos sensibles: solo los lee el dueno (ver reglas).
    tx.create(refPerfil.collection("privado").doc("datos"), {
      dni_hash: dniHash,
      fecha_nacimiento: d.fechaNacimiento,
      acepto_terminos_en: FieldValue.serverTimestamp(),
    });
  });

  return { ok: true };
});
