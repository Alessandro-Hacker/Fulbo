// Borrar la cuenta desde la app: Apple lo exige a toda app que permita crear
// cuentas, y la Ley 29733 da al usuario el derecho de cancelacion de sus datos.
import { onCall } from "firebase-functions/v2/https";
import { getAuth } from "firebase-admin/auth";
import { db, exigirSesion, refUsuario } from "./comun";

export const borrarCuenta = onCall(async (req) => {
  const uid = exigirSesion(req);
  const perfil = refUsuario(uid);
  const privado = (await perfil.collection("privado").doc("datos").get()).data();

  // Borra perfil, subcolecciones y el registro del DNI (para que pueda volver
  // a registrarse). Las calificaciones que emitio se conservan sin datos personales.
  await db.recursiveDelete(perfil);
  if (privado?.dni_hash) await db.doc(`dni_registrados/${privado.dni_hash}`).delete();
  await db.doc(`reputacion/${uid}`).delete();
  await getAuth().deleteUser(uid);
  return { ok: true };
});
