package com.origin.app;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;

@CapacitorPlugin(name="OriginSecrets")
public class OriginSecretsPlugin extends Plugin {
    private static final String ALIAS="origin_ai_key_v1";
    protected javax.crypto.SecretKey key() throws Exception {
        KeyStore store=KeyStore.getInstance("AndroidKeyStore");store.load(null);
        if(!store.containsAlias(ALIAS)) {
            KeyGenerator generator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            generator.init(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build());
            generator.generateKey();
        }
        return (javax.crypto.SecretKey)store.getKey(ALIAS,null);
    }
    @PluginMethod public void set(PluginCall call) {
        String id=call.getString("id"),value=call.getString("value");
        if(id==null || value==null){call.reject("Invalid key input");return;}
        try {
            android.content.SharedPreferences prefs=getContext().getSharedPreferences("origin_secrets",0);
            if(value.isEmpty()){prefs.edit().remove(id).commit();call.resolve();return;}
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,key());cipher.updateAAD(id.getBytes(StandardCharsets.UTF_8));
            String encrypted=Base64.encodeToString(cipher.getIV(),Base64.NO_WRAP)+":"+Base64.encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)),Base64.NO_WRAP);
            if(!prefs.edit().putString(id,encrypted).commit())throw new Exception("Storage failed");
            call.resolve();
        }catch(Exception e){call.reject("无法安全保存 API Key");}
    }
    @PluginMethod public void get(PluginCall call) {
        String id=call.getString("id");if(id==null){call.reject("Missing key id");return;}
        try {
            String encrypted=getContext().getSharedPreferences("origin_secrets",0).getString(id,"");
            JSObject result=new JSObject();
            if(encrypted.isEmpty()){result.put("value","");call.resolve(result);return;}
            String[] parts=encrypted.split(":");Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,Base64.decode(parts[0],Base64.NO_WRAP)));cipher.updateAAD(id.getBytes(StandardCharsets.UTF_8));
            result.put("value",new String(cipher.doFinal(Base64.decode(parts[1],Base64.NO_WRAP)),StandardCharsets.UTF_8));call.resolve(result);
        }catch(Exception e){call.reject("无法读取已保存的 API Key，请重新填写");}
    }
}
