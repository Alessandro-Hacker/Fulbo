// Duenos de cancha y canchas.
//
// Un dueno se registra solo desde la app (lo permiten las reglas), pero el
// documento nace con verificado=false y las reglas NO le dejan publicar
// ninguna cancha hasta que un administrador lo apruebe aqui. El permiso de
// administrador es un custom claim { admin: true }, el mismo que usa
// esAdmin() en firestore.rules; se asigna con el Admin SDK:
//   getAuth().setCustomUserClaims(uid, { admin: true })
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { onDocumentCreated, onDocumentUpdated } from "firebase-functions/v2/firestore";
import { geohashForLocation } from "geofire-common";
import { db, FieldValue, exigirSesion, notificar } from "./comun";

/** Exige sesion, correo verificado y el claim de administrador. */
function exigirAdmin(req: Parameters<typeof exigirSesion>[0]): string {
  const uid = exigirSesion(req);
  if (req.auth?.token.admin !== true) {
    throw new HttpsError("permission-denied", "Solo un administrador puede hacer esto.");
  }
  return uid;
}

/**
 * Aprueba o revoca a un dueno de cancha. Sin esta funcion el campo
 * 'verificado' no tiene ningun camino para llegar a true: las reglas solo
 * dejan al dueno cambiar razon_social y telefono.
 *
 * Al revocar, las canchas del dueno se desactivan (activa=false) para que
 * dejen de aparecer en la busqueda. No se borran: un partido pasado puede
 * seguir apuntando a ellas.
 */
export const verificarDueno = onCall(async (req) => {
  const adminUid = exigirAdmin(req);
  const { duenoUid, aprobar, motivo } = req.data as {
    duenoUid: string;
    aprobar: boolean;
    motivo?: string;
  };

  if (typeof duenoUid !== "string" || duenoUid.length === 0) {
    throw new HttpsError("invalid-argument", "Falta duenoUid.");
  }
  if (typeof aprobar !== "boolean") {
    throw new HttpsError("invalid-argument", "Falta aprobar.");
  }

  const dRef = db.doc(`duenos_cancha/${duenoUid}`);
  const dSnap = await dRef.get();
  if (!dSnap.exists) throw new HttpsError("not-found", "Ese dueno no existe.");
  if (dSnap.get("verificado") === aprobar) {
    return { ok: true, sinCambios: true, canchasDesactivadas: 0 };
  }

  await dRef.update({
    verificado: aprobar,
    verificado_por: adminUid,
    verificado_en: FieldValue.serverTimestamp(),
    verificado_motivo: typeof motivo === "string" ? motivo.slice(0, 300) : null,
  });

  let canchasDesactivadas = 0;
  if (!aprobar) {
    const canchas = await db.collection("canchas")
      .where("dueno_uid", "==", duenoUid)
      .where("activa", "==", true)
      .get();
    const batch = db.batch();
    canchas.forEach((c) => batch.update(c.ref, { activa: false }));
    await batch.commit();
    canchasDesactivadas = canchas.size;
  }

  await notificar(
    [duenoUid],
    aprobar ? "Tu local fue verificado" : "Tu local ya no esta verificado",
    aprobar
      ? "Ya puedes publicar tus canchas en FULBO."
      : "Tus canchas dejaron de aparecer en la busqueda.",
    { tipo: "verificacion_dueno" },
  );

  return { ok: true, sinCambios: false, canchasDesactivadas };
});

/**
 * El geohash permite buscar canchas por cercania (mismo patron que
 * alCrearPartido). Lo calcula el servidor: las reglas no dejan al cliente
 * escribir este campo, para que nadie coloque su cancha donde no esta.
 */
export const alCrearCancha = onDocumentCreated("canchas/{canchaId}", async (event) => {
  const snap = event.data;
  if (!snap) return;
  const ubicacion = snap.get("ubicacion");
  if (!ubicacion) return;

  await snap.ref.update({
    geohash: geohashForLocation([ubicacion.latitude, ubicacion.longitude]),
    rating: null,        // sin resenas todavia: la app muestra "Sin calificar"
    num_resenas: 0,
  });
});

/**
 * Si el dueno mueve el pin de su cancha, el geohash guardado queda viejo y la
 * busqueda por cercania deja de encontrarla. La guarda del if evita que la
 * escritura de esta funcion se dispare a si misma en bucle.
 */
export const alMoverCancha = onDocumentUpdated("canchas/{canchaId}", async (event) => {
  const despues = event.data?.after;
  if (!despues) return;
  const ubicacion = despues.get("ubicacion");
  if (!ubicacion) return;

  const nuevo = geohashForLocation([ubicacion.latitude, ubicacion.longitude]);
  if (despues.get("geohash") === nuevo) return;

  await despues.ref.update({ geohash: nuevo });
});
