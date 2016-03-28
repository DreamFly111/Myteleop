/*
 * Copyright (C) 2013 OSRF.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.rosjava.android_apps.teleop;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;
import com.google.common.collect.Lists;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;
import org.ros.android.view.VirtualJoystickView;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.CameraControlLayer;
import org.ros.android.view.visualization.layer.LaserScanLayer;
import org.ros.android.view.visualization.layer.Layer;
import org.ros.android.view.visualization.layer.OccupancyGridLayer;
import org.ros.android.view.visualization.layer.PathLayer;
import org.ros.android.view.visualization.layer.PosePublisherLayer;
import org.ros.android.view.visualization.layer.PoseSubscriberLayer;
import org.ros.android.view.visualization.layer.RobotLayer;
import org.ros.message.MessageListener;
import org.ros.namespace.NameResolver;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

import java.io.IOException;

import std_msgs.*;
import std_msgs.String;

/**
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class MainActivity extends RosAppActivity {
	private RosImageView<sensor_msgs.CompressedImage> cameraView;
	private VirtualJoystickView virtualJoystickView;
	private Button backButton;

	private Listerner listerner;
	private ConnectedNode connectedNode;
	private SwitchNodemain switchNodemain;
	private Button onOff;
	private VisualizationView visualizationView;

	public MainActivity() {
		// The RosActivity constructor configures the notification title and ticker messages.
		super("android teleop", "android teleop");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		setDashboardResource(R.id.top_bar);
		setMainWindowResource(R.layout.main);
		super.onCreate(savedInstanceState);

        cameraView = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
        cameraView.setMessageType(sensor_msgs.CompressedImage._TYPE);
        cameraView.setMessageToBitmapCallable(new BitmapFromCompressedImage());
        virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		onOff = (Button)findViewById(R.id.onOff);
		onOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onOff();
			}
		});

		visualizationView = (VisualizationView)findViewById(R.id.visualizationView);
		visualizationView.getCamera().jumpToFrame("map");

		visualizationView.onCreate(Lists.<Layer>newArrayList(//÷±Ω”œ‘ æ
				new CameraControlLayer(),
				new OccupancyGridLayer("map"),
				new PathLayer("move_base/NavfnROS/plan"),
				new PathLayer("move_base_dynamic/NavfnROS/plan"),
				new LaserScanLayer("base_scan"),
				new PoseSubscriberLayer("simple_waypoints_server/goal_pose"),
				new PosePublisherLayer("simple_waypoints_server/goal_pose"),
				new RobotLayer("base_footprint")
		));


	}

	private void onOff() {
		switchNodemain.onStart(connectedNode);//?????
	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {
		
		super.init(nodeMainExecutor);
		listerner = new Listerner();
		switchNodemain = new SwitchNodemain();
		visualizationView.init(nodeMainExecutor);

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

        java.lang.String joyTopic = remaps.get(getString(R.string.joystick_topic));
        java.lang.String camTopic = remaps.get(getString(R.string.camera_topic));

		java.lang.String mapTopic = remaps.get(getString(R.string.map_topic));///////////////////////

        NameResolver appNameSpace = getMasterNameSpace();
        joyTopic = appNameSpace.resolve(joyTopic).toString();
        camTopic = appNameSpace.resolve(camTopic).toString();

		mapTopic = appNameSpace.resolve(mapTopic).toString();/////////////////////////

		cameraView.setTopicName(camTopic);
        virtualJoystickView.setTopicName(joyTopic);

		
		nodeMainExecutor.execute(cameraView, nodeConfiguration
				.setNodeName("android/camera_view"));
		nodeMainExecutor.execute(virtualJoystickView,
				nodeConfiguration.setNodeName("android/virtual_joystick"));

		nodeMainExecutor.execute(visualizationView, nodeConfiguration.setNodeName("android/map_view"));
		//nodeMainExecutor.execute(visualizationView,nodeConfiguration.setNodeName("android_15/visualization_view"));
		nodeMainExecutor.execute(listerner,nodeConfiguration.setNodeName("android_apps_teleop/listerner"));
        } catch (IOException e) {
            // Socket problem
        }

	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  menu.add(0,0,0,R.string.stop_app);

		  return super.onCreateOptionsMenu(menu);
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  super.onOptionsItemSelected(item);
		  switch (item.getItemId()){
		  case 0:
			  onDestroy();
			  break;
		  }
		  return true;
	  }
}
