Build:

run "make" or:

javac -Xlint:unchecked -d bin src/gui/Button.java src/gui/CheckBox.java src/gui/ComboBox.java src/gui/Label.java src/gui/ListPanel.java src/gui/Panel.java src/gui/TabbedPanel.java src/gui/TextArea.java src/gui/TextField.java src/math/Box2d.java src/math/Box2i.java src/math/Collision.java src/math/Vec2d.java src/math/Vec2i.java src/obj/Direction.java src/obj/EntitySpawnpoint.java src/obj/Map.java src/obj/NinePatchTexture.java src/obj/Teleporter.java src/obj/Texture.java src/obj/Tile.java src/obj/TileSet.java src/program/MainMenu.java src/program/Program.java src/Main.java

then

java -cp bin src.Main
