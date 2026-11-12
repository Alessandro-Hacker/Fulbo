// Recalcula la reputacion cada vez que entra una calificacion (diagrama 6).
// La app nunca escribe rep_*: las reglas lo impiden y esta funcion lo hace.
import { onDocumentCreated } from "firebase-functions/v2/firestore";
import { db, MIN_CALIFICACIONES_PARA_PUNTAJE, refUsuario } from "./comun";

const CRITERIOS = ["puntualidad", "asistencia", "pago", "trato"] as const;
const redondear = (x: number) => Math.round(x * 10) / 10;

export const alCrearCalificacion = onDocumentCreated(
  "partidos/{partidoId}/calificaciones/{calId}",
  async (event) => {
    const cal = event.data?.data();
    if (!cal) return;
    const receptor: string = cal.receptor_uid;
    const accRef = db.doc(`reputacion/${receptor}`); // sumas acumuladas, solo servidor

    await db.runTransaction(async (tx) => {
      const acc = (await tx.get(accRef)).data() ?? {
        n: 0, puntualidad: 0, asistencia: 0, pago: 0, trato: 0,
      };
      acc.n += 1;
      for (const c of CRITERIOS) acc[c] += cal[c];
      tx.set(accRef, acc);

      const prom = (c: (typeof CRITERIOS)[number]) => redondear(acc[c] / acc.n);
      const global = redondear(CRITERIOS.reduce((s, c) => s + acc[c] / acc.n, 0) / CRITERIOS.length);
      const mostrar = acc.n >= MIN_CALIFICACIONES_PARA_PUNTAJE;

      // Con menos de 5 calificaciones la app muestra "Nuevo jugador" (valor null).
      tx.update(refUsuario(receptor), {
        num_calificaciones: acc.n,
        rep_global: mostrar ? global : null,
        rep_puntualidad: mostrar ? prom("puntualidad") : null,
        rep_asistencia: mostrar ? prom("asistencia") : null,
        rep_pago: mostrar ? prom("pago") : null,
        rep_trato: mostrar ? prom("trato") : null,
      });
    });
  },
);
