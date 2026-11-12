// Suscripcion Premium (diagrama 7). La pasarela (Culqi o Niubiz) avisa por
// webhook cuando un cobro se aprueba o falla. Aqui solo queda el esqueleto:
// falta elegir pasarela y verificar su firma segun su documentacion.
import { onRequest } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { db, FieldValue, Timestamp, refUsuario } from "./comun";

const WEBHOOK_SECRET = defineSecret("PASARELA_WEBHOOK_SECRET");
const DIAS_PLAN = 30;
const DIAS_GRACIA = 3;

export const webhookPago = onRequest({ secrets: [WEBHOOK_SECRET] }, async (req, res) => {
  if (req.method !== "POST") { res.status(405).end(); return; }

  // TODO: verificar la firma del webhook con WEBHOOK_SECRET.value() tal como
  // lo indique la pasarela. Sin esta verificacion cualquiera se activa Premium.
  const firmaValida = false;
  if (!firmaValida) { res.status(401).send("firma invalida"); return; }

  // TODO: adaptar a los campos reales del evento de la pasarela.
  const { tipo, usuarioUid, idPasarela, monto } = req.body as {
    tipo: "cobro_aprobado" | "cobro_fallido"; usuarioUid: string; idPasarela: string; monto: number;
  };
  const susRef = db.doc(`suscripciones/${idPasarela}`);

  if (tipo === "cobro_aprobado") {
    const vence = Timestamp.fromMillis(Date.now() + DIAS_PLAN * 86_400_000);
    await db.runTransaction(async (tx) => {
      tx.set(susRef, {
        usuario_uid: usuarioUid, estado: "activa", monto, moneda: "PEN",
        id_pasarela: idPasarela, inicia_en: FieldValue.serverTimestamp(), vence_en: vence,
      }, { merge: true });
      tx.update(refUsuario(usuarioUid), { plan: "premium", plan_vence_en: vence });
    });
  } else {
    // Cobro rechazado: 3 dias de gracia antes de volver a gratuito
    // (la tarea programada debe bajar el plan cuando venza la gracia).
    await susRef.set({
      estado: "gracia",
      gracia_hasta: Timestamp.fromMillis(Date.now() + DIAS_GRACIA * 86_400_000),
    }, { merge: true });
  }
  res.status(200).send("ok");
});
