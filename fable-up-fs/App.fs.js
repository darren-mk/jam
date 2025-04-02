import { printf, toConsole } from "./fable_modules/fable-library-js.4.24.0/String.js";
import { Record } from "./fable_modules/fable-library-js.4.24.0/Types.js";
import { record_type, int32_type, string_type } from "./fable_modules/fable-library-js.4.24.0/Reflection.js";

toConsole(printf("hello"));

export class Person extends Record {
    constructor(username, email, age) {
        super();
        this.username = username;
        this.email = email;
        this.age = (age | 0);
    }
}

export function Person_$reflection() {
    return record_type("App.Person", [], Person, () => [["username", string_type], ["email", string_type], ["age", int32_type]]);
}

export const darren = new Person("dk", "dk@eml.com", 50);

