/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{purs,html,js}"],
    theme: {
        extend: {},
    },
    plugins: [require("daisyui")],
    daisyui: {
        themes: ["light"],
    },
}
