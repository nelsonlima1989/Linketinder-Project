export function calcularProximoId<T extends { id: number }>(
    registros: T[]
): number {
    if (registros.length === 0) {
        return 1;
    }

    return Math.max(...registros.map(registro => registro.id)) + 1;
}