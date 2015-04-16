package co.foodchain.changsvotingsystem;

/**
 * Created by justinruoff on 2/12/15.
 */
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.telephony.SmsManager;
        import android.telephony.SmsMessage;
        import android.util.Log;
        import android.widget.Toast;

        import com.parse.FindCallback;
        import com.parse.ParseObject;
        import com.parse.*;

        import java.util.List;

public class TextMessageReceiver extends BroadcastReceiver{

    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle=intent.getExtras();
        final int test;


        Object[] messages=(Object[])bundle.get("pdus");
        SmsMessage[] sms=new SmsMessage[messages.length];

        for(int n=0;n<messages.length;n++){
            sms[n]=SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        for(SmsMessage msg:sms){


            ReceiveSMSActivity.sendSMSMessage(msg.getOriginatingAddress(),msg.getMessageBody(),context);
            ReceiveSMSActivity.updateMessageBox("\nFrom: "+msg.getOriginatingAddress()+"\n"+
                    "Message: "+msg.getMessageBody()+"\n");


        }
    }


}