from __future__ import annotations
from dataclasses import dataclass
from fable_modules.fable_library.reflection import (TypeInfo, string_type, int32_type, record_type)
from fable_modules.fable_library.string_ import (to_console, printf)
from fable_modules.fable_library.types import Record

to_console(printf("hello"))

def _expr0() -> TypeInfo:
    return record_type("App.Person", [], Person, lambda: [("username", string_type), ("email", string_type), ("age", int32_type)])


@dataclass(eq = False, repr = False, slots = True)
class Person(Record):
    username: str
    email: str
    age: int

Person_reflection = _expr0

darren: Person = Person("dk", "dk@eml.com", 50)

