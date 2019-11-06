package com.surecn.moat.tools.setting;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-01-11
 * Time: 14:31
 */
public class SettingManager {

    private static SettingManager sSettingManager;

    public static SettingManager getInstance(Context context) {
        if (sSettingManager == null) {
            sSettingManager = new SettingManager();
        }
        sSettingManager.mContext = context;
        return sSettingManager;
    }

    private Context mContext;

    private HashMap<Class, SettingEntry> mSettings = new HashMap<Class, SettingEntry>();

    private SettingManager() {}

    public void registerSetting(Class baseCls) {
        if (baseCls == null) {
            return;
        }
        SettingEntry settingEntry = new SettingEntry(this, baseCls);
        mSettings.put(baseCls, settingEntry);
    }

    public void unregisterSetting(Class baseCls) {
        if (baseCls == null) {
            return;
        }
        SettingEntry settingEntry = mSettings.remove(baseCls);
        if (settingEntry != null) {
            settingEntry.release();
        }
    }

    public void save(Class baseCls) {
        SettingEntry settingEntry = mSettings.get(baseCls);
        if (settingEntry != null) {
            settingEntry.save();
        }
    }

    private static class SettingEntry implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SettingManager mSettingManager;

        private String mName;

        private String mBasePackage;

        private Class mBaseCls;

        private SettingEntry(SettingManager settingManager, Class baseCls) {
            mSettingManager = settingManager;
            mName = baseCls.getName();
            mBaseCls = baseCls;
            Class[] declaredClses = baseCls.getDeclaredClasses();
            mBasePackage = baseCls.getPackage().getName();
            SharedPreferences preferences = mSettingManager.mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
            loadClassPreference(preferences, baseCls);
            if (declaredClses != null) {
                for (Class cls : declaredClses) {
                    loadClassPreference(preferences, cls);
                }
            }
            preferences.registerOnSharedPreferenceChangeListener(this);
        }

        private void save() {
            Class[] declaredClses = mBaseCls.getDeclaredClasses();
            SharedPreferences preferences = mSettingManager.mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
            loadClassPreference(preferences, mBaseCls);
            if (declaredClses != null) {
                for (Class cls : declaredClses) {
                    loadClassPreference(preferences, cls);
                }
            }
        }

        private void release() {
            SharedPreferences preferences = mSettingManager.mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            int index = key.indexOf("+");
            if (index >= 0) {
                String clsName = mBasePackage + "." + key.substring(0, index);
                try {
                    Class cls = Class.forName(clsName);
                    Field field = cls.getDeclaredField(key.substring(index + 1));
                    loadValue(sharedPreferences, key, field);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadClassPreference(SharedPreferences preferences, Class cls) {
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Preference) {
                        String key = getPreferenceKey(cls, field);
                        if (!preferences.contains(key)) {
                            saveValue(preferences, key, field);
                        } else {
                            loadValue(preferences, key, field);
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void saveValue(SharedPreferences settingPreferences, String key, Field field) {
        Class type = field.getType();
        try {
            if (type == int.class) {
                int defaultValue = field.getInt(null);
                settingPreferences.edit().putInt(key, defaultValue).commit();
            } else if (type == String.class) {
                String defaultValue  = (String) field.get(null);
                settingPreferences.edit().putString(key, defaultValue).commit();
            } else if (type == boolean.class) {
                boolean defaultValue  = field.getBoolean(null);
                settingPreferences.edit().putBoolean(key, defaultValue).commit();
            } else if (type == float.class) {
                float defaultValue  = field.getFloat(null);
                settingPreferences.edit().putFloat(key, defaultValue).commit();
            } else if (type == long.class) {
                long defaultValue  = field.getLong(null);
                settingPreferences.edit().putLong(key, defaultValue).commit();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void loadValue(SharedPreferences settingPreferences, String key, Field field) {
        Class type = field.getType();
        try {
            if (type == int.class) {
                field.setInt(null, settingPreferences.getInt(key, 0));
            } else if (type == String.class) {
                field.set(null, settingPreferences.getString(key, ""));
            } else if (type == boolean.class) {
                field.setBoolean(null, settingPreferences.getBoolean(key, false));
            } else if (type == float.class) {
                field.setFloat(null, settingPreferences.getFloat(key, 0));
            } else if (type == long.class) {
                field.setLong(null, settingPreferences.getLong(key, 0));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getPreferenceKey(Class cls, Field field) {
        return cls.getName().substring(cls.getPackage().getName().length() + 1) + "+" + field.getName();
    }

}
