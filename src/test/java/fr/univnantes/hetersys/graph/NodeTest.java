package fr.univnantes.hetersys.graph;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import org.junit.Before;

public class NodeTest {

	Node graphLinear = null;
	Node graphLinearOneNode = null;
	Node graphCondition = null;
	Node graphConditionOneNode = null;
	Node graphCycle = null;
	Node graphCycleOneNode = null;
	
	@Before
	public void setUp() throws Exception {
		Node n0,n1,n2,n3,n4,n5;
		
		n0 = new Node("n0"){};
		n1 = new Node("n1"){};
		n2 = new Node("n2"){};
		n3 = new Node("n3"){};
		n0.addArc(new Arc("n0->n1",n1));
		n1.addArc(new Arc("n0->n1",n2));
		n2.addArc(new Arc("n0->n1",n3));
		graphLinear = n0;
		graphLinearOneNode = n2;
		
		
		n0 = new Node("n0"){};
		n1 = new Node("n1"){};
		n2 = new Node("n2"){};
		n3 = new Node("n3"){};
		n4 = new Node("n4"){};
		n0.addArc(new Arc("n0->n1",n1));
		n1.addArc(new Arc("n1->n2",n2));
		n1.addArc(new Arc("n1->n3",n3));
		n2.addArc(new Arc("n2->n4",n4));
		n3.addArc(new Arc("n3->n4",n4));
		graphCondition = n0;
		graphConditionOneNode = n2;
		
		n0 = new Node("n0"){};
		n1 = new Node("n1"){};
		n2 = new Node("n2"){};
		n3 = new Node("n3"){};
		n4 = new Node("n4"){};
		n5 = new Node("n5"){};
		n0.addArc(new Arc("n0->n1",n1));
		n1.addArc(new Arc("n1->n2",n2));
		n2.addArc(new Arc("n2->n3",n3));
		n3.addArc(new Arc("n3->n1",n1));
		n1.addArc(new Arc("n1->n4",n4));
		n4.addArc(new Arc("n4->n5",n5));
		graphCycle = n0;
		graphCycleOneNode = n5;
	}
	
	@Test
	public void testFindNodeWithNull() throws IOException{
		Node n = graphLinear.findNode(null);
		assertNull(n);
	}
	
	@Test
	public void testFindNodeIdExists() throws IOException{
		Node n = graphLinear.findNode(graphLinearOneNode.getId());
		assertNotNull(n);
		assertEquals(graphLinearOneNode, n);
	}
	
	@Test
	public void testFindNodeIdNotExists() throws IOException{
		Node n = graphLinear.findNode((new Node("not exist"){}).getId());
		assertNull(n);
	}
	
	@Test
	public void testFindNodeLinearExists() throws IOException{
		Node n = graphLinear.findNode(graphLinearOneNode);
		assertNotNull(n);
		assertEquals(graphLinearOneNode, n);
	}
	
	@Test
	public void testFindNodeLinearNotExists() throws IOException{
		Node n = graphLinear.findNode(new Node("not exist"){});
		assertNull(n);
	}
	
	@Test
	public void testFindNodeConditionExists() throws IOException{
		Node n = graphCondition.findNode(graphConditionOneNode);
		assertNotNull(n);
		assertEquals(graphConditionOneNode, n);
	}
	
	@Test
	public void testFindNodeConditionNotExists() throws IOException{
		Node n = graphCondition.findNode(new Node("not exist"){});
		assertNull(n);
	}
	
	@Test
	public void testFindNodeCycleExists() throws IOException{
		Node n = graphCycle.findNode(graphCycleOneNode);
		assertNotNull(n);
		assertEquals(graphCycleOneNode, n);
	}
	
	@Test
	public void testFindNodeCycleNotExists() throws IOException{
		Node n = graphCycle.findNode(new Node("not exist"){});
		assertNull(n);
	}
}
