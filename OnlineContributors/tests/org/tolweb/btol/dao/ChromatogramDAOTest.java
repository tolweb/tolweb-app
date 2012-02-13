package org.tolweb.btol.dao;

import org.tolweb.btol.Chromatogram;
import org.tolweb.btol.ChromatogramBatch;
import org.tolweb.btol.PCRReaction;
import org.tolweb.dao.ApplicationContextTestAbstract;

public class ChromatogramDAOTest extends ApplicationContextTestAbstract {
	private PCRReactionDAO reactionDAO;
	private ChromatogramBatchDAO chroDAO;
	public ChromatogramDAOTest(String name) {
		super(name);
		reactionDAO = (PCRReactionDAO) context.getBean("pcrReactionDAO");
		chroDAO = (ChromatogramBatchDAO) context.getBean("chromatogramBatchDAO");
	}
	
	public void testChromatograms() {
		PCRReaction reaction = new PCRReaction();
		reaction.setBtolCode("BTOL00000");
		reaction.setReactionResult(0);
		reactionDAO.saveReaction(reaction);
		
		
		Chromatogram chro = new Chromatogram();
		chro.setFilename("test.ab1");
		chroDAO.saveChromatogram(chro);
		
		ChromatogramBatch batch = new ChromatogramBatch();
		batch.setName("plate 1");
		batch.setDescription("1st BTOL plate");
		chroDAO.saveChromatogramBatch(batch, 1L);
		
		batch.addToChromatograms(chro);
		chroDAO.saveChromatogramBatch(batch, 1L);
		
		ChromatogramBatch otherBatch = chroDAO.getChromatogramBatchWithId(batch.getId());
		assertEquals(otherBatch.getChromatograms().size(), batch.getChromatograms().size());
		assertEquals(((Chromatogram) otherBatch.getChromatograms().iterator().next()).getId(), chro.getId());
		
		reaction.addToChromatograms(chro);
		reactionDAO.saveReaction(reaction);
		
		PCRReaction otherReaction = (PCRReaction) reactionDAO.getObjectWithId(PCRReaction.class, reaction.getId());
		assertEquals(otherReaction.getChromatograms().size(), reaction.getChromatograms().size());
		assertEquals(((Chromatogram) otherReaction.getChromatograms().iterator().next()).getId(), chro.getId());		
	}
}
