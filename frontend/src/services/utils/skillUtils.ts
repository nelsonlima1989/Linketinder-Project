export function converterSkills(skills: string): string[] {
    return skills
        .split(",")
        .map(skill => skill.trim())
        .filter(skill => skill !== "");
}