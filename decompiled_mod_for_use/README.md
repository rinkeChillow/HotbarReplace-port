# HotbarReplace (Decompiled Source)

This project contains the completely decompiled source code and resources for the Minecraft Fabric mod **HotbarReplace** (version 0.1.4).

Decompiled using **CFR 0.152** Java decompiler.

## Project Structure

- `src/main/java/`: Java source code.
  - `xyz/twokilohertz/HotbarReplace.java`: Client mod initializer.
  - `xyz/twokilohertz/mixin/ItemStackMixin.java`: Mixins targeting ItemStack usage to replace depleted hotbar items.
- `src/main/resources/`: Configuration files and mod assets.
  - `assets/hotbarreplace/icon.png`: Mod icon.
  - `fabric.mod.json`: Fabric mod definition.
  - `hotbarreplace.mixins.json`: Mixin configuration.
  - `client-HotbarReplace-refmap.json`: Refmap for mixin mappings.
  - `LICENSE_HotbarReplace`: Mod license.

## How to use in IntelliJ IDEA (on another machine)

Since this machine does not have IntelliJ, you can copy the entire folder containing this project to your destination machine and follow these steps to import it:

### Option A: Import as Source Directories in an existing Fabric Mod Project (Recommended)
Because this mod is extremely small (only 2 Java classes and resources), the easiest way to work with it is to copy these files into a clean Fabric mod template:
1. Generate a clean Fabric mod workspace (e.g., using the [Fabric Template Mod](https://github.com/FabricMC/fabric-example-mod)).
2. Copy the contents of `src/main/java/` to the template's `src/main/java/` directory.
3. Copy the contents of `src/main/resources/` to the template's `src/main/resources/` directory.
4. Open the template project in IntelliJ IDEA. IntelliJ will automatically detect and set up the gradle wrapper, SDK, dependencies, and decompiled source code correctly.

### Option B: Import directly as a Java/Gradle Project
If you want to view/edit the source code directly without compiling or running Minecraft:
1. Open IntelliJ IDEA.
2. Select **File -> Open...** and navigate to this folder.
3. If IntelliJ doesn't automatically detect the project structure:
   - Go to **File -> Project Structure -> Modules**.
   - Select the module and click the **Sources** tab.
   - Mark the `src/main/java` directory as **Sources** (Blue folder icon).
   - Mark the `src/main/resources` directory as **Resources** (Purple folder icon with resource pattern).
   - Click **OK**.
4. Configure your Project SDK to use JDK 17 or higher (Java 21/24 recommended for modern Fabric mods) under **Project Structure -> Project -> SDK**.

---
*Note: The decompiler output may contain mapped yarn/intermediary Minecraft obfuscation class names (e.g. `class_1799`, `class_1657`). To resolve these names to human-readable names (e.g. `ItemStack`, `PlayerEntity`), it is best to import these source files into a Fabric development environment configured with yarn mappings.*
