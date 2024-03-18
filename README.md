Create Low-Heated modifies Create's heating system, exchanging the passive heaters for an active one.

It changes the following:

- Adds Basic Burner, a block that takes solid fuels and produces low heat.
- Adds new heat condition, "lowheated" can now be a Heat requirement inside recipes that use the Basin (in addition to the default "heated" and "superheated").
- When a charcoal Burner heats a steam engine step-up, it produces the amount that passive heaters would've done.
- Passive heating has been disabled completely [the tagged blocks and the unfed Blaze Burner].
- If needed, the Charcoal Burner can be overly empowered by placing a max-RPM'ed Encased Fan against it. When empowered, the Charcoal Burner produces heat at the same level as a Kindled Blaze Burner, but in exchange, it consumes way too much fuel [100x].

This project was designed to work in tandem with [Crete Picky Wheels](https://github.com/zehmaria/createpickywheels) [plus, the intent is to pair it with capacity nerfed windmills and waterwheels, using Create's default config].

# Links

Curseforge: https://curseforge.com/minecraft/mc-mods/create-low-heated

Modrinth: https://modrinth.com/mod/create-low-heated

Screenshots:  https://github.com/zehmaria/createlowheated/tree/mc1.19.2/dev/screenshots