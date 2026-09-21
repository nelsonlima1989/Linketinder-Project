export function renderizarSidebar(): string {
    return `
        <aside class="sidebar">

            <div class="sidebar-logo">
                <h1>LINKETINDER</h1>
                <span>Plataforma de talentos</span>
            </div>

            <nav class="sidebar-menu">

                <button class="menu-item ativo" data-tela="empresa">
                    <span>Empresa</span>
                </button>

                <button class="menu-item" data-tela="candidato">
                    <span>Candidato</span>
                </button>

                <div class="menu-separador"></div>

                <span class="menu-titulo">Cadastros</span>

                <button class="menu-item" data-tela="cadastro-candidato">
                    <span>Novo candidato</span>
                </button>

                <button class="menu-item" data-tela="cadastro-empresa">
                    <span>Nova empresa</span>
                </button>

            </nav>

        </aside>
    `;
}