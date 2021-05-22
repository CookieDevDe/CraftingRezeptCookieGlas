package de.obvcookie.craftingrezepte.Commands;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Content extends ItemStack {

    public Content(Material var1) {
        super(var1);
    }

    public Content(Material var1, Integer var2) {
        super(var1, var2);
    }

    public Content(Material var1, int var2, short var3) {
        super(var1, var2, var3);
    }

    public Content(String var1) {
        super(Material.PLAYER_HEAD, 1, (short) 3);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/" + var1).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemMeta headMeta = super.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        HeadReflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        super.setItemMeta(headMeta);
    }

    public Content(String var1, Integer var2) {
        super(Material.PLAYER_HEAD, var2, (short) 3);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", var1).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemMeta headMeta = super.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        HeadReflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        super.setItemMeta(headMeta);
    }

    public void setName(String var1) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("§r" + var1);
        setItemMeta(meta);
    }

    public void setLore(Boolean var1) {
        if (!var1) super.getItemMeta().setLore(null);
        else super.getItemMeta().setLore(Arrays.asList(""));
    }

    public void setLore(Integer var1, String var2) {
        ItemMeta meta = getItemMeta();
        var1 = var1 - 1;
        if (!getItemMeta().hasLore()) {
            final List<String> LIST = new ArrayList<>();
            for (int x = 0; x < var1 + 1; x++) LIST.add("");
            LIST.set(var1, "§r" + var2);
            meta.setLore(LIST);
        } else {
            final List<String> LIST = meta.getLore();
            int a = var1 - LIST.size();
            for (int x = 0; x < a + 1; x++) LIST.add("");
            LIST.set(var1, "§r" + var2);
            meta.setLore(LIST);
        }
        setItemMeta(meta);
    }

    public void setColor(Color var1) {
        if (!(super.getItemMeta() instanceof LeatherArmorMeta)) return;
        if (!(super.getItemMeta() instanceof LeatherArmorMeta)) return;
        LeatherArmorMeta meta = (LeatherArmorMeta) super.getItemMeta();
        meta.setColor(var1);
        super.setItemMeta(meta);
    }

    public void setColor(Integer var1, Integer var2, Integer var3) {
        if (!(super.getItemMeta() instanceof LeatherArmorMeta)) return;
        LeatherArmorMeta meta = (LeatherArmorMeta) super.getItemMeta();
        meta.setColor(Color.fromRGB(var1, var2, var3));
        super.setItemMeta(meta);
    }

    public static Content valueOf(ItemStack var1) {
        Content content = new Content(var1.getType(), var1.getAmount(), var1.getDurability());
        return content;
    }

    @Override @Deprecated
    public boolean setItemMeta(ItemMeta itemMeta) {
        return super.setItemMeta(itemMeta);
    }

    @Override @Deprecated
    public ItemMeta getItemMeta() {
        return super.getItemMeta();
    }
}
final class HeadReflections {

    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
    // Variable replacement
    private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");

    private HeadReflections() {
    }

    /**
     * Expand variables such as "{nms}" and "{obc}" to their corresponding packages.
     *
     * @param name the full name of the class
     * @return the expanded string
     */
    private static String expandVariables(String name) {
        StringBuffer output = new StringBuffer();
        Matcher matcher = MATCH_VARIABLE.matcher(name);

        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement;

            // Expand all detected variables
            if ("nms".equalsIgnoreCase(variable))
                replacement = NMS_PREFIX;
            else if ("obc".equalsIgnoreCase(variable))
                replacement = OBC_PREFIX;
            else if ("version".equalsIgnoreCase(variable))
                replacement = VERSION;
            else
                throw new IllegalArgumentException("Unknown variable: " + variable);

            // Assume the expanded variables are all packages, and append a dot
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement += ".";
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    /**
     * Retrieve a class by its canonical name.
     *
     * @param canonicalName the canonical name
     * @return the class
     */
    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    /**
     * Retrieve a class from its full name.
     * <p/>
     * Strings enclosed with curly brackets such as {TEXT} will be replaced according
     * to the following table:
     * <p/>
     * <table border="1">
     * <tr>
     * <th>Variable</th>
     * <th>Content</th>
     * </tr>
     * <tr>
     * <td>{nms}</td>
     * <td>Actual package name of net.minecraft.server.VERSION</td>
     * </tr>
     * <tr>
     * <td>{obc}</td>
     * <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
     * </tr>
     * <tr>
     * <td>{version}</td>
     * <td>The current Minecraft package VERSION, if any.</td>
     * </tr>
     * </table>
     *
     * @param lookupName the class name with variables
     * @return the looked up class
     * @throws IllegalArgumentException If a variable or class could not be found
     */
    public static Class<?> getClass(String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }

    /**
     * Search for the first publicly and privately defined constructor of the given name and parameter count.
     *
     * @param className lookup name of the class, see {@link #getClass(String)}
     * @param params    the expected parameters
     * @return an object that invokes this constructor
     * @throws IllegalStateException If we cannot find this method
     */
    public static ConstructorInvoker getConstructor(String className, Class<?>... params) {
        return getConstructor(getClass(className), params);
    }

    /**
     * Search for the first publicly and privately defined constructor of the given name and parameter count.
     *
     * @param clazz  a class to start with
     * @param params the expected parameters
     * @return an object that invokes this constructor
     * @throws IllegalStateException If we cannot find this method
     */
    public static ConstructorInvoker getConstructor(Class<?> clazz, Class<?>... params) {
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), params)) {

                constructor.setAccessible(true);
                return arguments -> {
                    try {
                        return constructor.newInstance(arguments);
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                    }
                };
            }
        }
        throw new IllegalStateException(String.format(
                "Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
    }

    /**
     * Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
     *
     * @param name the name of the class, excluding the package
     * @throws IllegalArgumentException If the class doesn't exist
     */
    public static Class<?> getCraftBukkitClass(String name) {
        return getCanonicalClass(OBC_PREFIX + "." + name);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    the target type
     * @param name      the name of the field, or NULL to ignore
     * @param fieldType a compatible field type
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className lookup name of the class, see {@link #getClass(String)}
     * @param name      the name of the field, or NULL to ignore
     * @param fieldType a compatible field type
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    the target type
     * @param fieldType a compatible field type
     * @param index     the number of compatible fields to skip
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className lookup name of the class, see {@link #getClass(String)}
     * @param fieldType a compatible field type
     * @param index     the number of compatible fields to skip
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
        return getField(getClass(className), fieldType, index);
    }

    // Common method
    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                // A function for retrieving a specific field value
                return new FieldAccessor<T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T get(Object target) {
                        try {
                            return (T) field.get(target);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public boolean hasField(Object target) {
                        // target instanceof DeclaringClass
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param clazz      a class to start with
     * @param methodName the method name, or NULL to skip
     * @param params     the expected parameters
     * @return an object that invokes this specific method
     * @throws IllegalStateException If we cannot find this method
     */
    public static MethodInvoker getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        return getTypedMethod(clazz, methodName, null, params);
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param clazz      a class to start with
     * @param methodName the method name, or NULL to skip
     * @param returnType the expected return type, or NULL to ignore
     * @param params     the expected parameters
     * @return an object that invokes this specific method
     * @throws IllegalStateException If we cannot find this method
     */
    public static MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if ((methodName == null || method.getName().equals(methodName)) &&
                    (returnType == null) || method.getReturnType().equals(returnType) &&
                    Arrays.equals(method.getParameterTypes(), params)) {

                method.setAccessible(true);
                return new MethodInvoker() {
                    @Override
                    public Object invoke(Object target, Object... arguments) {
                        try {
                            return method.invoke(target, arguments);
                        } catch (Exception e) {
                            throw new RuntimeException("Cannot invoke method " + method, e);
                        }
                    }
                };
            }
        }
        // Search in every superclass
        if (clazz.getSuperclass() != null)
            return getMethod(clazz.getSuperclass(), methodName, params);
        throw new IllegalStateException(String.format(
                "Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }
    /**
     * An interface for invoking a specific constructor.
     */
    public interface ConstructorInvoker {
        /**
         * Invoke a constructor for a specific class.
         *
         * @param arguments the arguments to pass to the constructor.
         * @return the constructed object.
         */
        public Object invoke(Object... arguments);
    }

    /**
     * An interface for invoking a specific method.
     */
    public interface MethodInvoker {
        /**
         * Invoke a method on a specific target object.
         *
         * @param target    the target object, or NULL for a static method.
         * @param arguments the arguments to pass to the method.
         * @return the return value, or NULL if is void.
         */
        public Object invoke(Object target, Object... arguments);
    }

    /**
     * An interface for retrieving the field content.
     *
     * @param <T> field type
     */
    public interface FieldAccessor<T> {
        /**
         * Retrieve the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @return the value of the field
         */
        public T get(Object target);

        /**
         * Set the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @param value  the new value of the field
         */
        public void set(Object target, Object value);

        /**
         * Determine if the given object has this field.
         *
         * @param target the object to test
         * @return TRUE if it does, FALSE otherwise
         */
        public boolean hasField(Object target);
    }

}