"""Known public plant reference, blinded headers, unknowns and provenance failures."""
from pathlib import Path
import tempfile
import unittest
from carbon_worker import fasta_records
from reference_catalog import ReferenceCatalog

FIXTURE = Path(__file__).resolve().parents[2] / 'data/genomics/references/U91966.1'


class ReferenceTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.catalog = ReferenceCatalog(self.root / 'catalog')
        self.fasta = FIXTURE / 'reference.fasta'
        self.annotation = FIXTURE / 'reference.embl'
        self.sequence = list(fasta_records(self.fasta))[0][1]

    def absorb(self, fasta=None, annotation=None):
        fasta = fasta or self.fasta
        return self.catalog.absorb(fasta, annotation or self.annotation,
            'https://www.ebi.ac.uk/ena/browser/view/U91966.1', fasta_records(fasta))

    def test_actual_plant_record_and_duplicate_absorption(self):
        first = self.absorb()
        second = self.absorb()
        self.assertEqual(first['reference']['organism'], 'Arabidopsis thaliana')
        self.assertEqual(first['reference']['gene'], ['rbcL'])
        self.assertEqual(first['reference']['taxon'], ['taxon:3702'])
        self.assertEqual(first['reference']['bases'], 2187)
        self.assertEqual(first['entry_sha256'], second['entry_sha256'])
        self.assertEqual(second['catalog_entries'], 1)

    def test_no_header_or_name_based_identification(self):
        self.assertEqual(self.catalog.identify(self.sequence)['status'], 'unknown_reference')
        self.absorb()
        result = self.catalog.identify(self.sequence)
        self.assertEqual(result['matches'][0]['accession'], 'U91966.1')
        self.assertFalse(result['query_header_used'])
        misleading = self.root / 'human.fasta'
        misleading.write_text('>definitely a different organism\n' + self.sequence)
        identity = self.catalog.identify(list(fasta_records(misleading))[0][1])
        self.assertEqual(identity['matches'][0]['organism'], 'Arabidopsis thaliana')

    def test_reverse_complement_and_unmatched_change(self):
        self.absorb()
        reverse = self.sequence.translate(str.maketrans('ACGT', 'TGCA'))[::-1]
        self.assertEqual(self.catalog.identify(reverse)['matches'][0]['strand'], 'reverse_complement')
        changed = ('A' if self.sequence[0] != 'A' else 'C') + self.sequence[1:]
        self.assertEqual(self.catalog.identify(changed)['status'], 'unknown_reference')
        self.assertEqual(self.catalog.identify(self.sequence[:100])['status'], 'unknown_reference')

    def test_annotation_mismatch_is_not_absorbed(self):
        wrong = self.root / 'wrong.fasta'
        wrong.write_text('>sample\n' + self.sequence[:-1] + ('A' if self.sequence[-1] != 'A' else 'C'))
        with self.assertRaisesRegex(ValueError, 'does not exactly match'):
            self.absorb(fasta=wrong)

    def test_corruption_is_not_silently_used(self):
        receipt = self.absorb()
        Path(receipt['entry_path']).write_text('{}')
        with self.assertRaisesRegex(ValueError, 'checksum failure'):
            self.catalog.identify(self.sequence)

    def test_source_artifact_corruption_is_detected(self):
        receipt = self.absorb()
        Path(receipt['reference']['annotation_artifact']).write_text('changed')
        with self.assertRaisesRegex(ValueError, 'artifact checksum failure'):
            self.catalog.identify(self.sequence)


if __name__ == '__main__':
    unittest.main()
