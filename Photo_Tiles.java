import org.opencv.core.Core;
import org.opencv.core.Range;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Photo_Tiles {
    public static void main(String args[]) {
        //Configurations://///////////////

        //Image to use:
        String file = "image.jpg";
        //Dimensions of tiles:
        int Width = 5;
        int height = 10;
        //spacing between tiles):
        double spacing = .5;
        //rows and columns:
        int rows = 1;
        int columns = 4;

        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat photo = imageCodecs.imread(file);
        double[] white = {255,255,255};
        double totalWidth = (Width*columns+(columns-1)*spacing);
        double totalHeight = (height*rows+(rows-1)*spacing);

        // Crop image to fit aspect ratio
        if(totalWidth/totalHeight > (double)photo.width()/(double)photo.height()){
            int newHeight = (int)((photo.width()*totalHeight)/totalWidth);
            photo = new Mat(photo, new Range((photo.height()-newHeight)/2,photo.height() - ((photo.height()-newHeight)/2)), new Range(0,photo.width()));
        }else if(totalWidth/totalHeight < (double)photo.width()/(double)photo.height()){
            int newWidth = (int)((photo.height()*totalWidth)/totalHeight);
            photo = new Mat(photo, new Range(0, photo.height()), new Range((photo.width()-newWidth)/2,photo.width() - ((photo.width()-newWidth)/2)));
        }

        //make a white image to put the tiles on for an example
        Mat base = photo.clone();
        for(int x = 0; x < base.width(); x++){
            for(int y = 0; y <base.height(); y++){
                base.put(y,x, white);
            }
        }

        int tileWidth = photo.width()/columns;
        int tileHeight = photo.height()/rows;
        double pixelSpacing = 0;
        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                Mat tile;
                int top = 0, bottom = 0, left = 0,  right = 0;
                double xspace = 0, yspace = 0;
                if(columns > 1 && rows > 1){
                    pixelSpacing = Math.max(photo.width()*(spacing/(Width*columns)), photo.height()*(spacing/(height*rows)));
                    xspace =  pixelSpacing * (columns-1);
                    xspace /= columns;
                    yspace =  pixelSpacing * (rows-1);
                    yspace /= rows;
                }else if(rows > 1){
                    pixelSpacing = photo.height()*(spacing/(height*rows));
                    yspace =  pixelSpacing * (rows-1);
                    yspace /= rows;
                    xspace =  0;
                }else{
                    pixelSpacing = photo.width()*(spacing/(Width*columns));
                    xspace =  pixelSpacing * (columns-1);
                    xspace /= columns;
                    yspace =  0;
                }

                // calculate how much to cut of each edge of the image
                top = (int)((double)y/(rows-1)*yspace);
                bottom = (int)((double) ((rows-1)-y)/(rows-1)*yspace);
                left = (int)((double)x/(columns-1)*xspace);
                right = (int)((double) ((columns-1)-x)/(columns-1)*xspace);

                // make the tile and output it as in image
                tile = new Mat(photo, new Range(y*tileHeight+top,y*tileHeight+tileHeight-bottom), new Range(x*tileWidth+left,x*tileWidth+tileWidth-right));

                // make the example image
                imageCodecs.imwrite("tiles/tile"+(y*columns+x+1)+".jpg", tile);
                for(int i = 0; i < tile.height(); i++){
                    for(int v = 0; v < tile.width(); v++){
                        base.put(y*tileHeight+top+y + i, x*tileWidth+left+x + v,tile.get(i,v));
                    }
                }
            }
        }
        imageCodecs.imwrite("example.png", base);
    }
}
