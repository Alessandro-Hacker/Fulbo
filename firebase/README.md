# FULBO · backend Firebase

Todo lo que vive en Firebase y no en la app: reglas de seguridad, indices y Cloud Functions.

```
firebase/
├── firebase.json            configuracion de la CLI y de los emuladores
├── .firebaserc              alias de proyectos: dev (fulbo-dev) y prod (fulbo-prod)
├── firestore.rules          quien puede leer y escribir que
├── firestore.indexes.json   indices compuestos que piden las consultas
├── functions/               Cloud Functions en TypeScript (Node 22)
│   └── src/
│       ├── comun.ts         region, referencias, notificaciones push
│       ├── perfil.ts        completarPerfil (DNI con HMAC, edad >= 16)
│       ├── inscripciones.ts solicitarInscripcion, resolverSolicitud, darseDeBaja
│       ├── partidos.ts      alCrearPartido, cancelarPartido, finalizarPartido
│       ├── reputacion.ts    alCrearCalificacion (recalcula rep_*)
│       ├── programadas.ts   cambia estados por hora + recordatorio de 3 h
│       ├── pagos.ts         webhookPago (esqueleto, falta la pasarela)
│       └── cuenta.ts        borrarCuenta
└── tests/reglas.test.mjs    pruebas de las reglas contra el emulador
```

## Primera vez

```bash
npm install -g firebase-tools
firebase login
cd firebase
# edita .firebaserc con los IDs reales de tus proyectos
firebase use dev
cd functions && npm install && cd ..
firebase functions:secrets:set DNI_PEPPER          # pega una clave larga y aleatoria
firebase functions:secrets:set PASARELA_WEBHOOK_SECRET
```

## Desarrollo local (sin gastar nada)

```bash
cd firebase
firebase emulators:start          # UI en http://localhost:4000
cd tests && npm install && npm test   # prueba las reglas
```

Para los secretos en el emulador crea `functions/.secret.local` con
`DNI_PEPPER=cualquier-clave-de-prueba` (no se sube a git).

## Desplegar

```bash
firebase use dev
firebase deploy --only firestore:rules,firestore:indexes
firebase deploy --only functions
```

Requiere el plan Blaze en el proyecto (Cloud Functions no funciona en el plan gratuito).
Configura una alerta de presupuesto en Google Cloud antes de desplegar.
