# Gotchas

Non-obvious bugs and footguns, grouped by subsystem. Each entry: what it looks like, what is actually
happening, the fix. Check here **before** investigating something that "should work".

Most entries below are well-known Java behaviors that directly affect this design; entries marked
"Observed" were hit in this project. Add project-specific entries as you hit them (cross-cutting ones here; member-only ones in
`members/<name>/docs/NOTES.md`).

## Console input

**A prompt is skipped right after entering a number.** `Scanner.nextInt()` reads the number but leaves the
newline in the buffer, so the next `nextLine()` returns an empty string immediately. Fix: only ever use
`nextLine()` and parse the text (`Integer.parseInt(line.trim())`). This is why `InputHelper` exists.

**The app crashes with `NoSuchElementException` when input ends.** When input is piped from a file
(`java ... < input.txt`) and the file runs out, `nextLine()` throws `NoSuchElementException`. `InputHelper`
should catch it and exit cleanly with a message instead of crashing.

**Two `Scanner(System.in)` objects lose input.** Each `Scanner` buffers ahead, so a second one misses text the
first already read. Use the single `Scanner` inside `InputHelper`.

## File storage

**Trailing empty fields disappear when loading.** `"a|b||".split("\\|")` returns 2 elements, not 4, because
`split` drops trailing empty strings. Always use `line.split("\\|", -1)`.

**`split("|")` splits every character.** `|` is a regex OR operator. Escape it: `"\\|"`.

**A user typed `|` and now a whole record is broken.** The extra delimiter shifts every field after it. All
text input must reject `|` (`Validator.requireNoDelimiter`).

**Data seems to vanish or "file not found" between runs.** `data/` is resolved relative to the current
working directory. Running from inside `src/` or from an IDE with a different working directory reads a
different (or empty) `data/`. Always run from the repo root; set the IDE working directory to the repo root.

**A bad line in a data file disappears after the next save.** `Repository.load()` skips bad lines (and prints
`[INFO] Skipped bad data - ...` at startup), so they are not in memory. The next `save()` rewrites the file
from memory, so the skipped lines are gone for good. Fix the file by hand before changing anything if the
data matters. (Observed while testing the foundation.)

**Saved changes are lost after a restart.** A service changed an object but did not call the repository's
`save()`. Every changing service method must save every repository it touched.

## Java language

**Two equal strings compare as different.** `==` compares references for objects. Use `equals(...)`, or
`equalsIgnoreCase(...)` for usernames/emails.

**`Enum.valueOf("pending")` throws `IllegalArgumentException`.** Enum names are case-sensitive. Parse file
values with the exact `name()`; for user input, use numbered choices (`InputHelper.readEnum`).

**`LocalDate.parse("2026-2-5")` throws `DateTimeParseException`.** ISO format needs two digits for month and day
(`2026-02-05`). Catch it in `InputHelper.readDate` and re-prompt with the expected format.

**`0.1 + 0.2` prints `0.30000000000000004`.** Format weights for display with `String.format("%.2f", weight)`.
