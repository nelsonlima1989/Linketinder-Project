import type { Empresa } from "../../models/Empresa";
import { renderizarGraficoSkills } from "./renderizarGraficoSkills";

export function renderizarPerfilEmpresa(
    empresa: Empresa
): string {

    return `
        <section class="perfil-empresa">

            <header class="cabecalho-empresa">

                <h1>${empresa.nome}</h1>

                <p>${empresa.descricao}</p>

                <p>
                    <strong>Estado:</strong>
                    ${empresa.estado}
                </p>

               <div class="acoes-perfil">
                    <button id="botao-nova-vaga" type="button">Cadastrar nova vaga</button>   
                    <button id="botao-editar-empresa" type="button">Editar Perfil</button>                    
                    <button id="botao-excluir-empresa" type="button">Excluir conta</button>
               </div>

            </header>

            <section class="candidatos">

                <h2>Candidatos disponíveis</h2>

                <div id="lista-candidatos"></div>

            </section>

            <section class="vagas-empresa">

                <h2>Minhas vagas</h2>

                <div id="lista-vagas-empresa"></div>

            </section>

            ${renderizarGraficoSkills()}

        </section>
    `;
}