package juxxl.prestige.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import juxxl.prestige.Prestige;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.module.setting.Setting;
import juxxl.prestige.module.setting.StringSetting;

public final class ProfileManager {
   private final Gson g = new Gson();
   private Path configDir;

   public ProfileManager() {
      try {
         String home = System.getProperty("user.home");
         this.configDir = Paths.get(home, ".prestige", "configs");
         Files.createDirectories(this.configDir);
      } catch (Exception e) {
         this.configDir = Paths.get(System.getProperty("java.io.tmpdir"), "prestige_configs");
      }
   }

   public List<String> getAvailableConfigs() {
      List<String> list = new ArrayList<>();

      try {
         if (Files.exists(this.configDir)) {
            File[] files = this.configDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
               for (File f : files) {
                  String n = f.getName();
                  list.add(n.substring(0, n.length() - 5));
               }
            }
         }
      } catch (Exception var8) {
      }

      if (list.isEmpty()) {
         list.add("default");
      }

      return list;
   }

   public void loadProfile() {
      this.loadProfile("default");
   }

   public void loadProfile(String name) {
      if (name == null || name.trim().isEmpty()) {
         name = "default";
      }

      Path path = this.configDir.resolve(name + ".json");
      if (Files.isRegularFile(path)) {
         try {
            JsonObject profile = (JsonObject)this.g.fromJson(Files.readString(path), JsonObject.class);
            if (profile == null) {
               return;
            }

            for (Module module : Prestige.INSTANCE.getModuleManager().getModules()) {
               JsonElement moduleJson = profile.get(String.valueOf(Prestige.INSTANCE.getModuleManager().getModules().indexOf(module)));
               if (moduleJson != null && moduleJson.isJsonObject()) {
                  JsonObject moduleConfig = moduleJson.getAsJsonObject();
                  JsonElement enabledJson = moduleConfig.get("enabled");
                  if (enabledJson != null && enabledJson.isJsonPrimitive()) {
                     module.setEnabled(enabledJson.getAsBoolean());
                  }

                  for (Setting<?> setting : module.getSettings()) {
                     JsonElement settingJson = moduleConfig.get(String.valueOf(module.getSettings().indexOf(setting)));
                     if (settingJson != null) {
                        if (setting instanceof BooleanSetting booleanSetting) {
                           booleanSetting.setValue(settingJson.getAsBoolean());
                        } else if (setting instanceof ModeSetting<?> modeSetting) {
                           modeSetting.setModeIndex(settingJson.getAsInt());
                        } else if (setting instanceof NumberSetting numberSetting) {
                           numberSetting.setValue(settingJson.getAsDouble());
                        } else if (setting instanceof KeybindSetting keybindSetting) {
                           int savedKey = settingJson.getAsInt();
                           keybindSetting.setKey(savedKey);
                           if (keybindSetting.isModuleKey() && savedKey != -1) {
                              module.setKey(savedKey);
                           }
                        } else if (setting instanceof StringSetting stringSetting) {
                           stringSetting.setValue(settingJson.getAsString());
                        } else if (setting instanceof MinMaxSetting minMaxSetting && settingJson.isJsonObject()) {
                           JsonObject minMaxObject = settingJson.getAsJsonObject();
                           double minValue = minMaxObject.get("1").getAsDouble();
                           double maxValue = minMaxObject.get("2").getAsDouble();
                           minMaxSetting.setMinValue(minValue);
                           minMaxSetting.setMaxValue(maxValue);
                        }
                     }
                  }
               }
            }
         } catch (Exception var23) {
         }
      }
   }

   public void saveProfile() {
      this.saveProfile("default");
   }

   public void saveProfile(String name) {
      if (name == null || name.trim().isEmpty()) {
         name = "default";
      }

      try {
         Files.createDirectories(this.configDir);
         Path path = this.configDir.resolve(name + ".json");
         JsonObject profile = new JsonObject();

         for (Module module : Prestige.INSTANCE.getModuleManager().getModules()) {
            JsonObject moduleConfig = new JsonObject();
            moduleConfig.addProperty("enabled", module.isEnabled());

            for (Setting<?> setting : module.getSettings()) {
               if (setting instanceof BooleanSetting booleanSetting) {
                  moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), booleanSetting.getValue());
               } else if (setting instanceof ModeSetting<?> modeSetting) {
                  moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), modeSetting.getModeIndex());
               } else if (setting instanceof NumberSetting numberSetting) {
                  moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), numberSetting.getValue());
               } else if (setting instanceof KeybindSetting keybindSetting) {
                  moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), keybindSetting.getKey());
               } else if (setting instanceof StringSetting stringSetting) {
                  moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), stringSetting.getValue());
               } else if (setting instanceof MinMaxSetting minMaxSetting) {
                  JsonObject minMaxObject = new JsonObject();
                  minMaxObject.addProperty("1", minMaxSetting.getMinValue());
                  minMaxObject.addProperty("2", minMaxSetting.getMaxValue());
                  moduleConfig.add(String.valueOf(module.getSettings().indexOf(setting)), minMaxObject);
               }
            }

            profile.add(String.valueOf(Prestige.INSTANCE.getModuleManager().getModules().indexOf(module)), moduleConfig);
         }

         Files.writeString(path, this.g.toJson(profile));
      } catch (Exception var16) {
      }
   }

   public void deleteProfile(String name) {
      if (name != null && !name.trim().isEmpty()) {
         try {
            Path path = this.configDir.resolve(name + ".json");
            Files.deleteIfExists(path);
         } catch (Exception var3) {
         }
      }
   }
}
