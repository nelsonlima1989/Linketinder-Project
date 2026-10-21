
import { ITENS_MENU, obterTelaInicial } from "../../services/utils/navegacaoService";

function renderizarItemMenu(tela: string, titulo: string): string {
    const classeAtiva = tela === obterTelaInicial() ? " ativo" : "";

    return `
        <button class="menu-item${classeAtiva}" data-tela="${tela}">
            <span>${titulo}</span>
        </button>
    `;
}

export function renderizarSidebar(): string {
    const itensPrincipais = ITENS_MENU
        .filter(item => item.tela === "empresa" || item.tela === "candidato")
        .map(item => renderizarItemMenu(item.tela, item.titulo))
        .join("");

    const itensCadastro = ITENS_MENU
        .filter(item => item.tela.startsWith("cadastro-"))
        .map(item => renderizarItemMenu(item.tela, item.titulo))
        .join("");

    return `
        <aside class="sidebar">

            <div class="sidebar-logo">
                <h1>LINKETINDER</h1>
                <span>Plataforma de talentos</span>
            </div>

            <nav class="sidebar-menu">

                ${itensPrincipais}

                <div class="menu-separador"></div>

                <span class="menu-titulo">Cadastros</span>

                ${itensCadastro}

            </nav>

        </aside>
    `;
}
