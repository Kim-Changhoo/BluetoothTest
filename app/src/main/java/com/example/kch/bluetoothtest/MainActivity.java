package com.example.kch.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = null;
    private final static int REQUEST_ENABLE_BT = 233;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    /*
     BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevice;
    // 스마트폰과 페어링 된 디바이스 간 통신 채널에 대응하는 BluetoothSocket


    EditText mEditReceive, mEditSend;
    Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditReceive = (EditText)findViewById(R.id.receiveString);
        mEditSend = (EditText)findViewById(R.id.sendString);
        mButtonSend = (Button)findViewById(R.id.sendButton);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendData()
                CheckBluetooth();
            }
        });
    }

    void CheckBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 블루투스 어댑터 얻기, 장치의 블루투스 어댑터 반환
        if(mBluetoothAdapter == null){
            // 장치가 블루투스를 지원하지 않는 경우.
            Toast.makeText(getApplicationContext(),"기기가 블루투스를 지원하지 않습니다.",Toast.LENGTH_LONG).show();
            finish();   // 어플리케이션 종료
       }
        else{
            // 장치가 블루투스를 지원하는 경우
            // .isEnabled() : ture 지원, flase 미지원
            if(!mBluetoothAdapter.isEnabled()){
                // 블루투스를 지원하지만 비활성인 상태인 경우
                // 블루투스를 활성상태로 바꾸기 위해 사용자 동의 요청
                Toast.makeText(getApplicationContext(),"현재 블루투스가 비활성 상태입니다.",Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else{
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링된 기기 목록을 보여주고 연결할 장치를 선택
                selectDevice();
            }
        }
    }

    void selectDevice(){
        mDevices = mBluetoothAdapter.getBondedDevices();    // 페어링 된 장치 목록 얻기
        mPairedDeviceCount = mDevices.size();   // 페어링 된 장치 수

        if(mPairedDeviceCount == 0) {     // 페어링 된 장치 없는 경우
            Toast.makeText(getApplicationContext(), "페어링 된 장치가 없습니다.",Toast.LENGTH_LONG).show();
            finish();
        }
        // 페어링 된 장치가 있는 경우
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices){
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환
            listItems.add(device.getName());
        }
        listItems.add("취소");    // 취소 항목 추가

        // CharSequence : 변경 가능한 문자열.
        // toArray : List 형태로 넘어온 것을 배열로 바꿔서 처리하기 위한 toArray().
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if(item == mPairedDeviceCount){
                    // 연결할 장치를 선택하지 않고 '취소'를 누를 경우
                    // 선택한 Item의 순서에 해당하는 정수 데이터가 넘어오는 듯
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.

                }
            }
        });

        builder.setCancelable(false);   // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_ENABLE_BT :
                if(resultCode == RESULT_OK){
                    // 블루투스가 활성 상태로 변경됨
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED){
                    // 블루투스가 비활성 상태임
                    Toast.makeText(getApplicationContext(),"블루투스를 사용할 수 없어 프로그램을 종료합니다.",Toast.LENGTH_LONG).show();
                    finish();   // 어플리케이션 종료
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
