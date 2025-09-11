Welcome to my opus project, a 2D game engine written from scratch in Java. The project is compiled, so you can put 'java -cp bin src.Main' in terminal in the project directory to run. Additionally, there is a Makefile so you make also choose to run 'make' with no target to compile and run. If all else fails, you can directly type the compile and run scripts as folows:

javac -Xlint:unchecked -d bin src/gui/Button.java src/gui/CheckBox.java src/gui/ComboBox.java src/gui/Label.java src/gui/ListPanel.java src/gui/Panel.java src/gui/TabbedPanel.java src/gui/TextArea.java src/gui/TextField.java src/math/Box2d.java src/math/Box2i.java src/math/Collision.java src/math/Vec2d.java src/math/Vec2i.java src/obj/Direction.java src/obj/EntitySpawnpoint.java src/obj/Map.java src/obj/NinePatchTexture.java src/obj/Teleporter.java src/obj/Texture.java src/obj/Tile.java src/obj/TileSet.java src/program/MainMenu.java src/program/Program.java src/Main.java

then,

java -cp bin src.Main

--------------------------------------------------------------------------------------------------------------------------

Keybindings and other input functionality in the project are as follows:
In EDITOR:

Left click -> Select/use current tool on map
Right click -> Drag map
Scroll wheel -> Resize map
1 -> Draw tool
2 -> Erase tool
3 -> Select tool
Shift+1 -> Select layer 0
Shift+2 -> Select layer 1
Shift+3 -> Select layer 2
Shift+4 -> Select layer 3
Ctrl+Z -> Undo map change
Ctrl+Y -> Redo map change
Ctrl+S -> Save current map (must have been saved with a name previously)

IN GAME:

WASD -> Player movement
Space -> Attack
