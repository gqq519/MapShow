package com.gqq.mapshow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private GeoCoder mGeoCoder;
    private TextView mTvShow;
    private LocationClient mLocationClient;
    private LatLng mLocation;// 定位的位置
    private String mLocationAddr;// 定位的地址
    private LatLng mCurrentTarget;// 当前的位置
    private String mCurrentAddr;// 当前位置的地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        // 初始化写到了Application里面
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // 主要是对地图控件和其他控件进行初始化
        initMapView();

        // 主要是为了设置地理编码
        initGeoCoder();

        // 主要是为了设置定位相关的信息
        initLocation();

    }

    // 主要是为了设置定位相关的信息
    private void initLocation() {
        /**
         * 1. 打开定位图层
         * 2. 初始化定位核心类LocationClient
         * 3. 设置定位的参数：定位的模式、定位返回的坐标类型、是不是需要地址信息等
         * 4. 设置定位的监听：定位在后台获取，为了拿到定位成功之后的信息（定位的经纬度、地址等）
         * 5. 开启定位（有的手机出现定位不到的时候，需要重新请求）
         */

        // 1. 打开定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 2. 初始化定位核心类LocationClient
        mLocationClient = new LocationClient(getApplicationContext());

        // 3. 设置定位的参数：坐标类型一定要设置成bd09ll，否则默认的坐标类型会有偏差
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置坐标的类型
        option.setIsNeedAddress(true);// 需要地址信息，默认不需要
        // 把参数信息设置给定位的核心类
        mLocationClient.setLocOption(option);

        // 4. 设置定位的监听
        mLocationClient.registerLocationListener(bDLocationListener);

        // 5. 开启定位
        mLocationClient.start();
    }

    //创建定位的监听：拿到定位的数据
    private BDLocationListener bDLocationListener = new BDLocationListener() {

        // 在这个方法里面，我们能拿到定位的信息
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                mLocationClient.requestLocation();// 去请求定位信息
                return;
            }

            // 拿到定位的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 将定位的经纬度信息创建出一个对象
            mLocation = new LatLng(latitude, longitude);
            mLocationAddr = bdLocation.getAddrStr();


            Log.e("TAG", "定位的地址：" + bdLocation.getAddrStr());

            // 创建的定位数据的对象：设置定位数据的参数
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100f)// 设置精度圈的大小
                    .latitude(latitude)// 设置经纬度
                    .longitude(longitude)
                    .build();
            // 设置定位的数据
            mBaiduMap.setMyLocationData(locationData);

        }
    };

    // 定位成功以后，移动到定位的地方
    private void moveToLocation() {

        /**
         * 要移动到地位的地方，移动的是地图，所以我们要设置地图的状态（更新）
         */

        // 不采用new MapStatus的方式，更方便查看传入的参数
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mLocation)// 设置定位的经纬度，地图展示到定位的经纬度处
                .zoom(19)// 地图缩放的级别3-21
                .build();

        // 创建MapStatusUpdate，原因是需要传入一个MapStatusUpdate
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 以动画方式更新地图的状态（更新的效果，从其他地方到定位的地方）
        mBaiduMap.animateMapStatus(update);

        // 这个是设置，并没有达到从之前的状态更新的一个效果
//        mBaiduMap.setMapStatus(update);
    }

    // 设置地理编码
    private void initGeoCoder() {
        // 创建地理编码查询的对象
        mGeoCoder = GeoCoder.newInstance();

        // 设置地理编码查询接口的监听：为了实时的拿到地理编码／反地理编码的结果
        mGeoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
    }

    // 创建出来的地理编码的查询监听
    private OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        // 拿到地理编码的结果：地址－－》经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 拿到反地理编码的结果：经纬度－－》地址信息
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            // 拿到的结果为空：编码错误，没有结果
            if (reverseGeoCodeResult == null) {
                mTvShow.setText("未知的位置");
                // 直接跳出，不再向下执行。
                return;
            }

            // 结果不为空：拿到了地址信息
            if (reverseGeoCodeResult != null) {
                // 取出地址信息
                mCurrentAddr = reverseGeoCodeResult.getAddress();
                // 给文本设置上
                mTvShow.setText("当前的位置：" + mCurrentAddr);
            }
        }
    };

    // 主要是对地图控件和其他控件进行初始化
    private void initMapView() {

        // 拿到布局里面的TextView
        mTvShow = (TextView) findViewById(R.id.tvShow);
        // 拿到定位的按钮
        Button btnLocation = (Button) findViewById(R.id.btnLocation);
        // 拿到导航的按钮
        Button btnNavigation = (Button) findViewById(R.id.btnNavigation);
        // 给定位按钮设置监听：目的是点击按钮，进行定位
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移动到定位的地方，展示定位
                moveToLocation();

            }
        });
        // 给导航的按钮设置点击监听：去调起百度地图的进行骑行导航，如果没有安装百度地图，打开网页导航
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 去进行导航

                /**
                 * 1. 起点：自己的位置，就是定位的位置
                 * 2. 终点：地图当前的位置：标注的位置
                 */
                startBikeNavigation(mLocation, mLocationAddr, mCurrentTarget, mCurrentAddr);
            }
        });

        // 找到地图的控件
        mMapView = (MapView) findViewById(R.id.bmapView);

        // 拿到地图的操作类
        mBaiduMap = mMapView.getMap();

        // 给地图设置状态的监听：目的：为了拿到地图的当前的状态（位置）
        mBaiduMap.setOnMapStatusChangeListener(mMapStatusChangeListener);

        // 扩展的内容
        // 设置Marker的监听（标注物的监听，可以监测到你点击了Marker）
        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);

    }

    // 创建Marker的监听：扩展内容
    private BaiduMap.OnMarkerClickListener mMarkerClickListener = new BaiduMap.OnMarkerClickListener() {

        // 当Marker被点击的时候会调用
        @Override
        public boolean onMarkerClick(final Marker marker) {

            // 1. 点击Marker跳转页面
//            Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
//            intent.putExtra("latlng",marker.getPosition().latitude+","+marker.getPosition().longitude);
//            startActivity(intent);

            /**
             * 2. 展示一个自定义的View
             */
//            mBaiduMap.clear();// 可以清空一下Marker，只展示一个InfoWindow
//
//            // 要展示的View
//            Button button = new Button(MainActivity.this);
//            button.setText("点击了Marker，展示的InfoWindow");
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//                    intent.putExtra("latlng", marker.getPosition().latitude + "," + marker.getPosition().longitude);
//                    startActivity(intent);
//                }
//            });
//            // 创建一个InfoWindow
//            InfoWindow infoWindow = new InfoWindow(button, marker.getPosition(), 0);
//            // 点击Marker，展示一个InfoWindow
//            mBaiduMap.showInfoWindow(infoWindow);

            /**
             * 3. 点击展示的是一个图标
             */
            // 创建出来的InfoWindow的图标
            BitmapDescriptor dotExp=BitmapDescriptorFactory.fromResource(R.mipmap.ic_icon);
            InfoWindow infoWindow = new InfoWindow(dotExp, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("latlng", marker.getPosition().latitude + "," + marker.getPosition().longitude);
                    startActivity(intent);
                }
            });
            mBaiduMap.showInfoWindow(infoWindow);

            return true;
        }
    };

    //创建的地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

        // 地图状态开始变化时
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        // 地图状态在变化过程中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        // 当地图状态变化结束时
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            // 我们在当前的方法里面去完成状态结束时，添加一个标注
            mCurrentTarget = mapStatus.target;
            Log.e("TAG", "当前的地图的位置:纬度" + mCurrentTarget.latitude + "经度" + mCurrentTarget.longitude);

            // 在地图状态发生变化的时候，添加覆盖物之前去请求反地理编码
            // 创建一个反地理编码的配置信息：只包含一个经纬度信息
            ReverseGeoCodeOption option = new ReverseGeoCodeOption();
            // 把要进行反地理编码的经纬度设置上
            option.location(mCurrentTarget);
            // 去发起反地理编码
            mGeoCoder.reverseGeoCode(option);

            // 当地图状态发生变化时，经纬度都已经拿到了，想在当前的位置添加一个标注，更明显的显示位置
            // 提供一个方法：实现添加标注，为了将每一个功能点分开
            mBaiduMap.clear();// 主要是为了清空地图上之前的覆盖物
            addMarker(mCurrentTarget);

        }
    };

    // 创建的用于展示覆盖物的图标：从mipmap里面拿到的
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.location);

    // 用于添加覆盖物的方法
    private void addMarker(LatLng latLng) {

        // 给覆盖物设置信息：位置（添加到哪里）、图标（用于展示的一个图标）
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);// 设置位置
        options.icon(dot);// 设置图标

        // 给地图添加覆盖物，并展示
        mBaiduMap.addOverlay(options);
    }

    // 进行骑行导航
    private void startBikeNavigation(LatLng startLatlng, String startAddr, LatLng endLatlng, String endAddr) {

        // 导航的数据：起点和终点的经纬度和地址信息
        NaviParaOption option = new NaviParaOption();
        option.startName(startAddr);
        option.startPoint(startLatlng);
        option.endName(endAddr);
        option.endPoint(endLatlng);
        // 打开百度地图的骑行导航:返回的类型时boolean，有没有调起成功
        boolean bikeNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, getApplicationContext());
        if (!bikeNavi) {
            // 没有调起成功，跳转到网页进行网页导航
            startWebNavigation(startLatlng, startAddr, endLatlng, endAddr);
        }
    }

    private void startWebNavigation(LatLng startLatlng, String startAddr, LatLng endLatlng, String endAddr) {

        // 导航的数据
        NaviParaOption option = new NaviParaOption();
        option.startName(startAddr);
        option.startPoint(startLatlng);
        option.endName(endAddr);
        option.endPoint(endLatlng);

        // 开启网页导航
        BaiduMapNavigation.openWebBaiduMapNavi(option, getApplicationContext());
        // 进行步行导航
//        BaiduMapNavigation.openBaiduMapWalkNavi(option, getApplicationContext());
        // gqq的MacBook Pro  密码：gqqzxc519

    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
