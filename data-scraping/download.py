from google_images_search import GoogleImagesSearch
from io import BytesIO
from PIL import Image
import pathlib
import random
import hashlib
import json
import argparse
import os

CREDS = json.load(open('credentials.json'))
SAVE_PATH = pathlib.Path('data/')

def main(term: str, count: int):
    gis = GoogleImagesSearch(CREDS['apikey'], CREDS['cx'])
    my_bytes_io = BytesIO()

    gis.search({'q': term, 'num': count})
    if len(gis.results()):
        os.system(f'mkdir -p "{SAVE_PATH / term}"')
        for image in gis.results():
            my_bytes_io.seek(0)
            raw_image_data = image.get_raw_data()
            image.copy_to(my_bytes_io, raw_image_data)
            image.copy_to(my_bytes_io)
            my_bytes_io.seek(0)
            temp_img = Image.open(my_bytes_io)

            imgnum = random.randint(1, 1000000000000)
            imgname = hashlib.sha256(str(imgnum).encode()).hexdigest()
            
            temp_img.save(SAVE_PATH / term / f'{imgname}.{temp_img.format}')
        
        print(f'saved {count} files in {SAVE_PATH / term}')
    else:
        print('sorry, not found anything...')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Download from google photos')
    parser.add_argument('-n', '--count', dest='count', required=True, help='number of photos to download.')
    parser.add_argument('-t', '--term', dest='term', required=True, help='term to search.')
    
    args = parser.parse_args()
    main(args.term, int(args.count))
