/*
*  Copyright (C) 2015 TzuTaLin
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.tzutalin.vision.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BigImageCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.tzutalin.vision.visionrecognition.ObjectDetector;
import com.tzutalin.vision.visionrecognition.R;
import com.tzutalin.vision.visionrecognition.VisionClassifierCreator;
import com.tzutalin.vision.visionrecognition.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;

public class ObjectDetectActivity extends Activity {
    private final static String TAG = "ObjectDetectActivity";
    private ObjectDetector mObjectDet;

    //This variable should be global not just onCreate now. (IU)
    private String imgPath;
    // UI
    MaterialListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_object_detect);
        mListView = (MaterialListView) findViewById(R.id.material_listview);
        final String key = Camera2BasicFragment.KEY_IMGPATH;
        imgPath = getIntent().getExtras().getString(key);
        if (!new File(imgPath).exists()) {
            Toast.makeText(this, "No file path", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        DetectTask task = new DetectTask();
        task.execute(imgPath);

        //Log.d(TAG, "Image path: "+imgPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_object_detect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ==========================================================
    // Tasks inner class
    // ==========================================================
    private class DetectTask extends AsyncTask<String, Void, List<VisionDetRet>> {
        private ProgressDialog mmDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mmDialog = ProgressDialog.show(ObjectDetectActivity.this, getString(R.string.dialog_wait),getString(R.string.dialog_object_decscription), true);
        }

        @Override
        protected List<VisionDetRet> doInBackground(String... strings) {

            final String filePath = strings[0];
            long startTime;
            long endTime;
            Log.d(TAG, "DetectTask filePath:" + filePath);
            if (mObjectDet == null) {
                try {

                    //System.err happening here.
                    mObjectDet = VisionClassifierCreator.createObjectDetector(getApplicationContext());
                    // TODO: Get Image's height and width
                    mObjectDet.init(0, 0);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            List<VisionDetRet> ret = new ArrayList<>();
            if (mObjectDet != null) {
                startTime = System.currentTimeMillis();
                Log.d(TAG, "Start objDetect");
                ret.addAll(mObjectDet.classifyByPath(filePath));
                Log.d(TAG, "end objDetect");
                endTime = System.currentTimeMillis();
                final double diffTime = (double) (endTime - startTime) / 1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ObjectDetectActivity.this, "Take " + diffTime + " second", Toast.LENGTH_LONG).show();
                    }
                });

                //Moved here b/c original location causing crash (JavaNullPointer exception).
                // Should only be done if not null anyway, which is what this case checks for.(IU)
                mObjectDet.deInit();
            }

            //Crashing here. Moved into if !=null above.
            //mObjectDet.deInit();
            return ret;
        }

        @Override
        protected void onPostExecute(List<VisionDetRet> rets) {
            super.onPostExecute(rets);
            if (mmDialog != null) {
                mmDialog.dismiss();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();

            int scaleAmt=4;

            //Set bitmap to be 1/scaleAmt size of the original image.
            options.inSampleSize = scaleAmt;

            //Issue: /sdcard is hardcoded in .so. That's where drawing is done.

            /*Updated to point to external storage. Do not use /sdcard path.
            Not universally recognized. So now, rectangles are now drawn on Java layer,
            rather than at OpenCV layer.(IU)
            */
            String retImgPath = imgPath;

            //String retImgPath = "/sdcard/temp.jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(retImgPath, options);

            //Initialize rectangle paint object
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);

            //Set it to stroke (no fill)
            paint.setStyle(Paint.Style.STROKE);

            //Initialize text paint object.
            Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint2.setColor(Color.BLUE);
            paint2.setTextSize(50);
            paint2.setStyle(Paint.Style.FILL);



            //Create mutable bitmap for painting on.
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            //Create canvas.
            Canvas canvas = new Canvas(mutableBitmap);

            /*
            Get width and height of captured image and drawing area. Will need
            to determine proportion for drawing rectangles on canvas.
            */
            int originalWidth = canvas.getWidth()*scaleAmt;
            int originalHeight = canvas.getHeight()*scaleAmt;
            int newHeight=canvas.getHeight();
            int newWidth=canvas.getWidth();

            //Draw a rectangle around each object identified. (IU)
            for (VisionDetRet item : rets)
            {
                if (!item.getLabel().equalsIgnoreCase("background")) {

                    //Calculate proportional coordinates.
                    RectF rect=new RectF(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());

                    RectF newRect=returnProportionalRectangle(rect, newWidth, newHeight, originalWidth, originalHeight);

                    canvas.drawRect(newRect.left,newRect.top,newRect.right,newRect.bottom,paint);

                    //Now, draw the text centered within the rectangle.
                    canvas.drawText(item.getLabel(),Math.round(newRect.centerX()),Math.round(newRect.centerY()),paint2);
                }
            }

            //Draw image with rectangles.
            Drawable d = new BitmapDrawable(getResources(), mutableBitmap);

            Card card = new Card.Builder(ObjectDetectActivity.this)
                    .withProvider(BigImageCardProvider.class)
                    .setDrawable(d)
                    .endConfig()
                    .build();
            mListView.add(card);

            //If nothing has been identified, we don't want to add any detail; just
            // a card saying "no object found"(IU)
            boolean noObjFound=true;

            for (VisionDetRet item : rets) {

                if (!item.getLabel().equalsIgnoreCase("background")) {
                    noObjFound=false;
                }
            }

            if (!noObjFound)
            {
                for (VisionDetRet item : rets) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getLabel())
                            .append(" probability = ").append(item.getConfidence()*100+"%");
                            /*.append(" [")
                            .append(item.getLeft()).append(',')
                            .append(item.getTop()).append(',')
                            .append(item.getRight()).append(',')
                            .append(item.getBottom())
                            .append(']');*/
                    Log.d(TAG, sb.toString());

                    if (!item.getLabel().equalsIgnoreCase("background")) {
                        card = new Card.Builder(ObjectDetectActivity.this)
                                .withProvider(BigImageCardProvider.class)
                                .setTitle("Detect Result")
                                .setDescription(sb.toString())
                                .endConfig()
                                .build();
                        mListView.add(card);
                    }
                }
            }

            else
            {
                card = new Card.Builder(ObjectDetectActivity.this)
                        .withProvider(BigImageCardProvider.class)
                        .setTitle("Nothing Recognized")
                        .setDescription("Engine did not recognize anything")
                        .endConfig()
                        .build();
                mListView.add(card);
            }

            File beDeletedFile = new File(retImgPath);
            if (beDeletedFile.exists()) {
                beDeletedFile.delete();
            }

        }
    }

    //Calculates a rectangle at new scale given some input at old scale.
    private RectF returnProportionalRectangle(RectF input, int newwidth, int newheight, int oldwidth, int oldheight)
    {

        float newHeightRatio = (float)newheight / oldheight;
        float newWidthRatio = (float)newwidth / oldwidth;

        RectF newRect=new RectF();

        newRect.top    = ((   input.top    ) * newHeightRatio);
        newRect.left   = ((   input.left   ) * newWidthRatio );

        newRect.bottom = ((input.bottom + 1) * newHeightRatio) - 1;
        newRect.right  = ((input.right  + 1) * newWidthRatio ) - 1;


        return newRect;
    }
}
