@echo off
echo Compiling ZeldaRPG...
if not exist out mkdir out

javac -d out -sourcepath src -encoding UTF-8 ^
  src/main/GameLauncher.java ^
  src/core/GameState.java ^
  src/core/KeyHandler.java ^
  src/core/GamePanel.java ^
  src/core/SaveManager.java ^
  src/core/Sound.java ^
  src/camera/Camera.java ^
  src/tile/Tile.java ^
  src/tile/TileManager.java ^
  src/entity/Entity.java ^
  src/entity/Player.java ^
  src/entity/NPC.java ^
  src/entity/ImageNPC.java ^
  src/entity/enemy/Enemy.java ^
  src/entity/enemy/Slime.java ^
  src/entity/enemy/Skeleton.java ^
  src/entity/enemy/DarkKnight.java ^
  src/entity/enemy/Samurai.java ^
  src/entity/enemy/Ninja.java ^
  src/entity/enemy/SunGuardian.java ^
  src/object/SuperObject.java ^
  src/object/OBJ_Sword.java ^
  src/object/OBJ_Shield.java ^
  src/object/OBJ_Potion.java ^
  src/object/OBJ_Chest.java ^
  src/object/OBJ_House.java ^
  src/object/OBJ_Church.java ^
  src/object/OBJ_CrystalGate.java ^
  src/combat/AttackHitbox.java ^
  src/combat/DamageNumber.java ^
  src/combat/Projectile.java ^
  src/ui/UI.java ^
  src/quest/Quest.java ^
  src/quest/QuestManager.java ^
  src/util/UtilityTool.java ^
  src/util/CollisionChecker.java ^
  src/util/AssetSetter.java

if %errorlevel% neq 0 (
    echo COMPILATION FAILED
    pause
    exit /b 1
)

echo Build successful! Launching game...
java -cp "out;src" main.GameLauncher
