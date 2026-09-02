package juxxl.prestige.module.setting;

public final class KeybindSetting extends Setting<KeybindSetting> {
   private int keyCode;
   private boolean listening;
   private final boolean moduleKey;
   private final int originalKey;

   public KeybindSetting(CharSequence name, int key) {
      this(name, key, false);
   }

   public KeybindSetting(CharSequence name, int key, boolean moduleKey) {
      super(name);
      this.keyCode = key;
      this.originalKey = key;
      this.moduleKey = moduleKey;
   }

   public boolean isModuleKey() {
      return this.moduleKey;
   }

   public boolean isListening() {
      return this.listening;
   }

   public int getOriginalKey() {
      return this.originalKey;
   }

   public void setListening(boolean listening) {
      this.listening = listening;
   }

   public int getKey() {
      return this.keyCode;
   }

   public void setKey(int key) {
      this.keyCode = key;
   }

   public void toggleListening() {
      this.listening = !this.listening;
   }
}
