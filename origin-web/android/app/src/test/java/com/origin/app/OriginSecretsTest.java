package com.origin.app;
import android.content.Context;
import com.getcapacitor.*;
import javax.crypto.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class OriginSecretsTest {
 static class Call extends PluginCall {
  JSObject result;String failure;boolean resolved;
  Call(String id,String value){super(null,"OriginSecrets","test","set",new JSObject().put("id",id).put("value",value));}
  @Override public void resolve(){resolved=true;}
  @Override public void resolve(JSObject data){resolved=true;result=data;}
  @Override public void reject(String error){failure=error;}
 }
 @Test public void encryptsAtRestRestoresAcrossPluginInstancesAndRejectsTampering() throws Exception {
  Context context=RuntimeEnvironment.getApplication();KeyGenerator generator=KeyGenerator.getInstance("AES");generator.init(256);SecretKey aes=generator.generateKey();
  OriginSecretsPlugin plugin=new OriginSecretsPlugin(){@Override public Context getContext(){return context;}@Override protected SecretKey key(){return aes;}};
  Call set=new Call("ai:https://fixture.invalid/v1","test-secret");plugin.set(set);assertTrue(set.resolved);
  String stored=context.getSharedPreferences("origin_secrets",0).getString("ai:https://fixture.invalid/v1","");assertFalse(stored.contains("test-secret"));assertTrue(stored.contains(":"));
  OriginSecretsPlugin restarted=new OriginSecretsPlugin(){@Override public Context getContext(){return context;}@Override protected SecretKey key(){return aes;}};
  Call get=new Call("ai:https://fixture.invalid/v1","");restarted.get(get);assertEquals("test-secret",get.result.getString("value"));
  context.getSharedPreferences("origin_secrets",0).edit().putString("other-endpoint",stored).commit();Call tampered=new Call("other-endpoint","");restarted.get(tampered);assertNotNull(tampered.failure);assertNull(tampered.result);
  Call clear=new Call("ai:https://fixture.invalid/v1","");restarted.set(clear);assertTrue(clear.resolved);assertFalse(context.getSharedPreferences("origin_secrets",0).contains("ai:https://fixture.invalid/v1"));
 }
}
