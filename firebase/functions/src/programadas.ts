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
    // Publicado + llego la hora: si el organizador confirmo que se juega
    // igual (se_juega=true, funcion confirmarPartidoIncompleto) pasa a En
    // juego aunque falten jugadores; si no, vence porque nunca se lleno.
    const publicados = await partidos.where("estado", "==", "publicado")
      .where("fecha_hora", "<=", ahora).get();
    // En calificacion + vencio la ventana de 48 h -> Cerrado
    const enCalif = await partidos.where("estado", "==", "en_calificacion")
      .where("calificacion_cierra_en", "<=", ahora).get();

    const batch = db.batch();
    completos.forEach((d) => batch.update(d.ref, { estado: "en_juego" }));
    publicados.forEach((d) => batch.update(d.ref, {
      estado: d.get("se_juega") === true ? "en_juego" : "vencido",
    }));
    enCalif.forEach((d) => batch.update(d.ref, { estado: "cerrado" }));
    await batch.commit();

    // Recordatorio 3 h antes (ventana de 15 min para no repetirlo)
    const desde = Timestamp.fromMillis(Date.now() + 165 * 60_000);
    const hasta = Timestamp.fromMillis(Date.now() + 180 * 60_000);
    // Incluye los partidos a medio llenar que el organizador confirmo:
    // 'in' + rango usa el mismo indice (estado, fecha_hora) que ya existe.
    const proximos = await partidos.where("estado", "in", ["completo", "publicado"])
      .where("fecha_hora", ">", desde).where("fecha_hora", "<=", hasta).get();
    const porJugar = proximos.docs.filter(
      (d) => d.get("estado") === "completo" || d.get("se_juega") === true);
    for (const p of porJugar) {
      const insc = await p.ref.collection("inscripciones").where("estado", "==", "aceptado").get();
      await notificar(insc.docs.map((d) => d.id), "Tu partido es en 3 horas",
        `${p.get("cancha_nombre") ?? "Revisa la cancha"} · lleva tu parte del pago.`,
        { partidoId: p.id, tipo: "recordatorio" });
    }
  },
);
