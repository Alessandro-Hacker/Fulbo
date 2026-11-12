// Tareas que corren solas cada 15 minutos: mueven los estados del partido
// segun la hora (diagrama 8) y mandan el recordatorio de 3 horas.
import { onSchedule } from "firebase-functions/v2/scheduler";
import { db, Timestamp, notificar } from "./comun";

export const tareasCadaQuinceMinutos = onSchedule(
  { schedule: "every 15 minutes", timeZone: "America/Lima" },
  async () => {
    const ahora = Timestamp.now();
    const partidos = db.collection("partidos");

    // Completo + llego la hora -> En juego
    const completos = await partidos.where("estado", "==", "completo")
      .where("fecha_hora", "<=", ahora).get();
    // Publicado + llego la hora -> Vencido (nunca se lleno).
    // Si el organizador decide jugar igual, la app llama a otra funcion
    // (pendiente: "se juega con menos jugadores").
    const publicados = await partidos.where("estado", "==", "publicado")
      .where("fecha_hora", "<=", ahora).get();
    // En calificacion + vencio la ventana de 48 h -> Cerrado
    const enCalif = await partidos.where("estado", "==", "en_calificacion")
      .where("calificacion_cierra_en", "<=", ahora).get();

    const batch = db.batch();
    completos.forEach((d) => batch.update(d.ref, { estado: "en_juego" }));
    publicados.forEach((d) => batch.update(d.ref, { estado: "vencido" }));
    enCalif.forEach((d) => batch.update(d.ref, { estado: "cerrado" }));
    await batch.commit();

    // Recordatorio 3 h antes (ventana de 15 min para no repetirlo)
    const desde = Timestamp.fromMillis(Date.now() + 165 * 60_000);
    const hasta = Timestamp.fromMillis(Date.now() + 180 * 60_000);
    const proximos = await partidos.where("estado", "==", "completo")
      .where("fecha_hora", ">", desde).where("fecha_hora", "<=", hasta).get();
    for (const p of proximos.docs) {
      const insc = await p.ref.collection("inscripciones").where("estado", "==", "aceptado").get();
      await notificar(insc.docs.map((d) => d.id), "Tu partido es en 3 horas",
        `${p.get("cancha_nombre") ?? "Revisa la cancha"} · lleva tu parte del pago.`,
        { partidoId: p.id, tipo: "recordatorio" });
    }
  },
);
