// Borrar la cuenta desde la app: Apple lo exige a toda app que permita crear
// cuentas, y la Ley 29733 da al usuario el derecho de cancelacion de sus datos.
import { onCall } from "firebase-functions/v2/https";
import { getAuth } from "firebase-admin/auth";
import { db, exigirSesion, refUsuario } from "./comun";

export const borrarCuenta = onCall(async (req) => {
  const uid = exigirSesion(req);
  const perfil = refUsuario(uid);

  // Borra el perfil, sus subcolecciones (privado, dispositivos) y las sumas
  // de reputacion. Las calificaciones que emitio se conservan, porque ya no
  // contienen datos personales de quien se va.
  await db.recursiveDelete(perfil);
  await db.doc(`reputacion/${uid}`).delete();
  await getAuth().deleteUser(uid);
  return { ok: true };
});
