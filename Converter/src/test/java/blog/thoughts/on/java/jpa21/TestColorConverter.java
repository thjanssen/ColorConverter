package blog.thoughts.on.java.jpa21;

import java.awt.Color;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import blog.thoughts.on.java.jpa21.converter.ColorConverter;
import blog.thoughts.on.java.jpa21.entity.RectangleEntity;

@RunWith(Arquillian.class)
public class TestColorConverter {

	@PersistenceContext
	private EntityManager em;

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap
				.create(JavaArchive.class)
				.addClasses(RectangleEntity.class, ColorConverter.class)
				.addAsManifestResource("META-INF/persistence.xml",
						"persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	@ShouldMatchDataSet(value = "data/rectangle.yml", excludeColumns = "id")
	public void createRectangle() {
		RectangleEntity rectangle = new RectangleEntity();
		rectangle.setX(100);
		rectangle.setY(50);
		rectangle.setColor(new Color(50, 100, 150, 200));

		this.em.persist(rectangle);
		Assert.assertNotNull(rectangle.getId());
	}

	@Test
	@UsingDataSet("data/rectangle.yml")
	public void searchSquare() {
		Color color = new Color(50, 100, 150, 200);
		RectangleEntity rectangle = this.em
				.createQuery("SELECT r FROM Rectangle r WHERE color=:color",
						RectangleEntity.class).setParameter("color", color)
				.getSingleResult();

		Assert.assertEquals(color, rectangle.getColor());
		Assert.assertEquals(new Integer(1), rectangle.getId());
		Assert.assertEquals(new Integer(100), rectangle.getX());
		Assert.assertEquals(new Integer(50), rectangle.getY());
	}
}
