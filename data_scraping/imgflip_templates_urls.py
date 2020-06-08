from firebase import firebase
import random
import json
from lxml import html
import requests

num = 50

def scrape_imageflip_to_dict(num):
	imgflip_url = 'https://imgflip.com/memetemplates?page={}'
	tepmlates_for_page = 40
	imgs_alts = []
	imgs_urls = []
	for j in range(num // 40 + 1):
		content = requests.get(imgflip_url.format(j)).content
		tree = html.fromstring(content)
		imgs_alts += tree.xpath('//img/@alt')[1:]
		imgs_urls += tree.xpath('//img/@src')[1:]

	results = []
	for i in range(num):
		template_name = imgs_alts[i].replace(' Meme Template', '')
		template_url = 'http:' + imgs_urls[i]
		record = {'title': template_name, 'path': template_url}
		results.append(record)
	return results
	

if __name__ == '__main__':
	url = 'https://filter-memes.firebaseio.com'
	firebase = firebase.FirebaseApplication(url, None)
	templates_db = 'templates/'
	for template in scrape_imageflip_to_dict(num):
		print(template)
		r = firebase.post(templates_db, template)
		print(r)

