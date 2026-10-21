
import { describe, expect, it } from "vitest";
import { escaparHtml } from "./escapeHtml";


describe("escaparHtml", () => {
    it("deve preservar textos comuns", () => {
        expect(escaparHtml("Desenvolvedor Full Stack"))
            .toBe("Desenvolvedor Full Stack");
    });

    it("deve escapar caracteres especiais do HTML", () => {
        expect(escaparHtml("<div class=\"teste\">A & B</div>"))
            .toBe("&lt;div class=&quot;teste&quot;&gt;A &amp; B&lt;/div&gt;");
    });

    it("deve escapar aspas simples", () => {
        expect(escaparHtml("João's"))
            .toBe("João&#39;s");
    });

    it("deve neutralizar tags de script", () => {
        expect(escaparHtml("<script>alert('teste')</script>"))
            .toBe("&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;");
    });

    it("deve preservar strings vazias", () => {
        expect(escaparHtml("")).toBe("");
    });
});
