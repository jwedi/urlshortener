

from locust import HttpUser, task, constant_throughput
import random
import string

letters = string.ascii_lowercase
class UrlShortenerUser(HttpUser):
    wait_time = constant_throughput(1)
    url_mappings = {"asd123": "http://youtube.com/asd"}

    @task(8)
    def get_url(self):
        with self.client.get(f"/{random.choice(list(self.url_mappings.keys()))}", allow_redirects=False, name="/{urlId}", catch_response=True) as response:
            if response.status_code == 303 or response.status_code == 404:
                response.success()

    @task(1)
    def create_url(self):
        random_long_url = ''.join(random.choice(letters) for i in range(8))
        response = self.client.post("/url", json={"originalUrl": f"http://{random_long_url}"})
        if response.status_code == 200:
            url_mapping = response.json()["urlMapping"]
            self.url_mappings[url_mapping["urlId"]] = url_mapping["originalUrl"]

