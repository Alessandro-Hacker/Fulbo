// Utilidades compartidas por todas las funciones.
import { initializeApp } from "firebase-admin/app";
import { getFirestore, FieldValue, Timestamp } from "firebase-admin/firestore";
import { getMessaging } from "firebase-admin/messaging";
import { setGlobalOptions } from "firebase-functions/v2";
import { HttpsError, CallableRequest } from "firebase-functions/v2/https";

initializeApp();

// Debe coincidir con la ubicacion de tu base Firestore (no se puede cambiar
// despues de crearla). southamerica-west1 = Santiago de Chile, la mas cercana a Lima.
export const REGION = "southamerica-west1";
setGlobalOptions({ region: REGION, maxInstances: 10 });

export const db = getFirestore();
export { FieldValue, Timestamp };

// ---- Valores permitidos (los mismos que usa la app en commonMain) ----------
export const NIVELES = ["recien", "casual", "intermedio", "competitivo"] as const;
export const POSICIONES = ["arquero", "defensa", "medio", "delantero"] as const;
export const MIN_CALIFICACIONES_PARA_PUNTAJE = 5;
export const HORAS_AVISO_CANCELACION = 3;
export const HORAS_VENTANA_CALIFICACION = 48;

export type EstadoPartido =
  | "publicado" | "completo" | "en_juego" | "en_calificacion"
  | "cerrado" | "cancelado" | "vencido";

export type EstadoInscripcion =
  | "pendiente" | "aceptado" | "rechazado" | "lista_espera" | "baja";

// ---- Referencias -------------------------------------------------------------
export const refUsuario = (uid: string) => db.doc(`usuarios/${uid}`);
export const refPartido = (id: string) => db.doc(`partidos/${id}`);
export const refInscripcion = (partidoId: string, uid: string) =>
  db.doc(`partidos/${partidoId}/inscripciones/${uid}`);

// ---- Seguridad ---------------------------------------------------------------
/** Exige sesion y correo verificado. Devuelve el uid. */
export function exigirSesion(req: CallableRequest<unknown>): string {
  if (!req.auth) throw new HttpsError("unauthenticated", "Inicia sesion.");
  if (req.auth.token.email_verified !== true) {
    throw new HttpsError("failed-precondition", "Verifica tu correo primero.");
  }
  return req.auth.uid;
}

export function texto(v: unknown, campo: string, max = 60): string {
  if (typeof v !== "string" || v.trim().length === 0 || v.length > max) {
    throw new HttpsError("invalid-argument", `Campo invalido: ${campo}`);
  }
  return v.trim();
}

// ---- Notificaciones push ----------------------------------------------------
/** Envia una notificacion a todos los dispositivos registrados de cada usuario. */
export async function notificar(
  uids: string[],
  titulo: string,
  cuerpo: string,
  datos: Record<string, string> = {},
): Promise<void> {
  const tokens: string[] = [];
  for (const uid of new Set(uids)) {
    const snap = await db.collection(`usuarios/${uid}/dispositivos`).get();
    snap.forEach((d) => tokens.push(d.id));
  }
  if (tokens.length === 0) return;

  const resp = await getMessaging().sendEachForMulticast({
    tokens,
    notification: { title: titulo, body: cuerpo },
    data: datos,
  });

  // Limpia tokens que ya no existen (app desinstalada, etc.)
  const borrar: Promise<unknown>[] = [];
  resp.responses.forEach((r, i) => {
    const code = r.error?.code ?? "";
    if (code.includes("registration-token-not-registered") || code.includes("invalid-argument")) {
      for (const uid of uids) {
        borrar.push(db.doc(`usuarios/${uid}/dispositivos/${tokens[i]}`).delete());
      }
    }
  });
  await Promise.allSettled(borrar);
}
