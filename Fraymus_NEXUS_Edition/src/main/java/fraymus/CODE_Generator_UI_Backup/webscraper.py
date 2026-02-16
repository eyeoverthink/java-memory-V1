
import requests
from bs4 import BeautifulSoup
import time
import json
from passive_learner import PassiveLearner
import random

class FraymusWebScraper:
    def __init__(self):
        self.learner = PassiveLearner()
        self.security_feeds = [
            'https://www.cisa.gov/known-exploited-vulnerabilities-catalog',
            'https://nvd.nist.gov/vuln/data-feeds',
            'https://blog.replit.com'
        ]
        self.headers = {
            'User-Agent': 'FraymusBot/1.0 (Security Research)'
        }
        
    def scrape_url(self, url):
        try:
            response = requests.get(url, headers=self.headers, timeout=10)
            if response.status_code == 200:
                soup = BeautifulSoup(response.text, 'html.parser')
                text_content = soup.get_text()
                chunks = [chunk.strip() for chunk in text_content.split('\n') if len(chunk.strip()) > 50]
                return self.analyze_security_content(chunks, url)
        except Exception as e:
            print(f"Error scraping {url}: {e}")
        return []

    def analyze_security_content(self, chunks, source):
        analyzed_data = {
            'source': source,
            'content': chunks,
            'security_relevance': self.calculate_security_score(chunks),
            'timestamp': time.time()
        }
        return analyzed_data

    def calculate_security_score(self, chunks):
        security_keywords = ['vulnerability', 'exploit', 'security', 'threat', 'attack']
        score = 0
        for chunk in chunks:
            chunk = chunk.lower()
            score += sum(chunk.count(keyword) for keyword in security_keywords)
        return min(1.0, score / 10)  # Normalize score between 0 and 1

    def continuous_learn(self):
        while True:
            # Rotate between security feeds and battle monitoring
            url = random.choice(self.security_feeds)
            data = self.scrape_url(url)
            if data and data['security_relevance'] > 0.3:  # Only learn significant security content
                self.learner.save_data(data)
            time.sleep(300)  # Wait 5 minutes between scrapes

if __name__ == "__main__":
    scraper = FraymusWebScraper()
    scraper.continuous_learn()
