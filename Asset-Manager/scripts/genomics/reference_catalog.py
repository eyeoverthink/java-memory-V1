"""Offline, reference-backed sequence recognition. No species inference from likelihood."""
import hashlib
import json
from pathlib import Path
import re
import uuid


def digest(data):
    return hashlib.sha256(data).hexdigest()


def put(directory, suffix, data):
    directory.mkdir(parents=True, exist_ok=True)
    key = digest(data)
    path = directory / (key + suffix)
    if path.exists():
        if path.read_bytes() != data:
            raise ValueError('Content-addressed archive integrity failure')
    else:
        temporary = directory / ('.' + uuid.uuid4().hex + '.tmp')
        temporary.write_bytes(data)
        temporary.replace(path)
    return path, key


class ReferenceCatalog:
    def __init__(self, root):
        self.root = Path(root)

    def absorb(self, fasta, annotation, source_url, records):
        records = list(records)
        if len(records) != 1:
            raise ValueError('A reference import requires exactly one FASTA record')
        sequence = records[0][1]
        raw = annotation.read_bytes()
        text = raw.decode('utf-8-sig')
        identity = re.search(r'^ID\s+(\w+); SV (\d+);.*? (\d+) BP\.', text, re.M)
        if not identity:
            raise ValueError('Expected an EMBL nucleotide record with accession, version and base count')
        accession = identity[1] + '.' + identity[2]
        if len(sequence) != int(identity[3]):
            raise ValueError('FASTA length differs from the annotated record')
        section = re.search(r'^SQ[^\n]*\n([\s\S]*?)^//', text, re.M)
        if not section:
            raise ValueError('Annotation has no sequence body')
        annotated_sequence = re.sub(r'[\s0-9]', '', section[1]).upper()
        if annotated_sequence != sequence or not re.fullmatch('[ACGT]+', sequence):
            raise ValueError('FASTA sequence does not exactly match its annotation')
        md5 = re.search(r'^DR\s+MD5; ([a-f0-9]+)\.', text, re.M)
        if md5 and hashlib.md5(sequence.encode('ascii')).hexdigest() != md5[1]:
            raise ValueError('Sequence differs from the archive MD5 checksum')
        qualifiers = {}
        for key, value in re.findall(r'^FT\s+/(\w+)="([\s\S]*?)"', text, re.M):
            qualifiers.setdefault(key, []).append(re.sub(r'\s*\nFT\s*', ' ', value).strip())
        organisms = qualifiers.get('organism', [])
        if len(set(organisms)) != 1:
            raise ValueError('Reference must have one explicit organism annotation')
        source_url = source_url.strip()
        if not source_url.startswith('https://'):
            raise ValueError('Reference source provenance requires an HTTPS URL')
        fasta_path, fasta_sha = put(self.root / 'artifacts', '.fasta', fasta.read_bytes())
        annotation_path, annotation_sha = put(self.root / 'artifacts', '.embl', raw)
        sequence_sha = digest(sequence.encode('ascii'))
        entry = {'schema_version': 1, 'accession': accession, 'organism': organisms[0],
                 'taxon': [v for v in qualifiers.get('db_xref', []) if v.startswith('taxon:')],
                 'gene': qualifiers.get('gene', []), 'product': qualifiers.get('product', []),
                 'organelle': qualifiers.get('organelle', []), 'strain': qualifiers.get('strain', []),
                 'features': [{'type': k, 'location': v.strip()} for k, v in
                              re.findall(r'^FT   (\w+)\s+([^\n]+)', text, re.M)],
                 'bases': len(sequence), 'sequence_sha256': sequence_sha,
                 'fasta_sha256': fasta_sha, 'annotation_sha256': annotation_sha,
                 'fasta_artifact': str(fasta_path.resolve()), 'annotation_artifact': str(annotation_path.resolve()),
                 'source_url': source_url, 'evidence_class': 'reference_annotation',
                 'verification': 'FASTA equals annotation sequence; archive MD5 checked when present',
                 'scope': 'known reference record; not an open-world organism classifier'}
        encoded = json.dumps(entry, sort_keys=True, indent=2).encode('utf-8')
        path, key = put(self.root / 'entries', '.json', encoded)
        return {'reference': entry, 'entry_path': str(path.resolve()), 'entry_sha256': key,
                'catalog_entries': len(list((self.root / 'entries').glob('*.json'))),
                'adapted_information': ['accession/version', 'source organism and gene annotations',
                                        'raw FASTA and annotation', 'exact sequence identity and provenance hashes'],
                'weights_updated': False}

    def identify(self, sequence):
        if not re.fullmatch('[ACGT]+', sequence):
            raise ValueError('Reference identification requires canonical DNA')
        forward = digest(sequence.encode('ascii'))
        reverse = digest(sequence.translate(str.maketrans('ACGT', 'TGCA'))[::-1].encode('ascii'))
        matches = []
        entries = sorted((self.root / 'entries').glob('*.json'))
        for path in entries:
            data = path.read_bytes()
            if digest(data) != path.stem:
                raise ValueError('Reference entry checksum failure: ' + str(path))
            entry = json.loads(data)
            if entry['sequence_sha256'] in (forward, reverse) and entry['bases'] == len(sequence):
                for field, sha_field in [('fasta_artifact', 'fasta_sha256'), ('annotation_artifact', 'annotation_sha256')]:
                    if digest(Path(entry[field]).read_bytes()) != entry[sha_field]:
                        raise ValueError('Reference artifact checksum failure')
                matches.append({'accession': entry['accession'], 'organism': entry['organism'],
                                'gene': entry['gene'], 'product': entry['product'],
                                'source_url': entry['source_url'], 'entry_sha256': path.stem,
                                'strand': 'forward' if entry['sequence_sha256'] == forward else 'reverse_complement',
                                'method': 'exact full-sequence SHA-256 match', 'evidence_class': 'reference_annotation'})
        return {'status': 'exact_reference_match' if matches else 'unknown_reference',
                'matches': matches, 'catalog_entries': len(entries), 'query_header_used': False,
                'scope': 'closed local reference catalog; shared sequences do not uniquely establish a species'}
