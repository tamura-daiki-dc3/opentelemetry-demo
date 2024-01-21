import pino from "pino";

export default function getLogger(name?: string) {
  const logger = pino(
    {
      // transport: {
      //   target: "pino-pretty" // jsonではなく単純な文字列として出力
      // },
      level: process.env.LOG_LEVEL ?? "info",
      name: name,
      formatters: {
        level: (label) => ({ level: label }), // default: { level: number }
        bindings: (bindings) => ({
          pid: bindings.pid,
          hostname: bindings.hostname,
          name: bindings.name,
        }),
      },
      timestamp: pino.stdTimeFunctions.isoTime,
    },
    pino.destination(2) // stderr
  );
  return logger;
}
