/**
 * 
 */
package com.gcorp.repository.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.PropertyPath;
import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.BaseEntity;
import com.gcorp.repository.BaseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author AE.GNAMIAN
 *
 */
@Slf4j
public class BaseRepositoryImpl<T extends BaseEntity, U extends Serializable> implements BaseRepository<T, U> {

	protected Class<T> clazz;
	protected EntityManager entityManager;
	protected SimpleJpaRepository<T, U> simpleJpaRepository;

	@SuppressWarnings("unchecked")
	public void initParameterizedType() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] typeArguments = parameterizedType.getActualTypeArguments();
			clazz = (Class<T>) typeArguments[0];
		}
		if (simpleJpaRepository == null) {
			simpleJpaRepository = new SimpleJpaRepository<>(clazz, entityManager);
		}
	}

	@PersistenceContext
	public void setEntityManager(EntityManager newEm) {
		this.entityManager = newEm;
	}

	private Sort defaultOrder() {
		try {
			initParameterizedType();
			return clazz.newInstance().defaultOrder();
		} catch (InstantiationException | IllegalAccessException e) {
			log.warn("Couldn't get default order: {}", e);
			return null;
		}
	}

	private boolean isValidProperty(Root<T> root, Sort.Order order) {
		try {
			return PropertyPath.getParametizedPath(root, order.getProperty()) != null;
		} catch (Exception e) {
			log.warn("Property couldn't be validated: {}", order.getProperty());
			log.error("Error on {}", e);
			return false;
		}
	}

	private CriteriaQuery<T> getQuery(CriteriaBuilder builder, CriteriaQuery<T> query, Root<T> root,
			Pageable pageable) {
		Sort sort = pageable == null || pageable.getSort() == null || Sort.unsorted().equals(pageable.getSort())
				? this.defaultOrder()
				: pageable.getSort();
		if (sort == null) {
			return query;
		}
		Map<String, Boolean> properties = sort.stream().filter(o -> isValidProperty(root, o))
				.collect(Collectors.toMap(Sort.Order::getProperty, o -> Direction.ASC.equals(o.getDirection())));
		return query.orderBy(properties.entrySet().stream()
				.map(p -> Boolean.TRUE.equals(p.getValue())
						? builder.asc(PropertyPath.getParametizedPath(root, p.getKey()))
						: builder.desc(PropertyPath.getParametizedPath(root, p.getKey())))
				.toArray(i -> new Order[i]));
	}

	protected EntityGraph<?> graph() {
		if (clazz == null) {
			initParameterizedType();
		}
		return entityManager.createEntityGraph(clazz);
	}

	@Override
	public Page<T> findByFilters(SearchFilters<T> searchFilters, Pageable pageable) {
		initParameterizedType(); // Initialize the clazz field with type of
									// parameter
		EntityGraph<?> entityGraph = graph();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);

		Root<T> root = query.from(clazz);
		query = getQuery(builder, query, root, pageable);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<T> countRoot = countQuery.from(clazz);
		if (searchFilters == null) {
			long total = entityManager.createQuery(countQuery)
					.setHint(SqlNamingConvention.Graph.PROPERTY_GRAPH, entityGraph).getSingleResult();
			List<T> content = (pageable != null)
					? entityManager.createQuery(query).setFirstResult((int) pageable.getOffset())
							.setMaxResults(pageable.getPageSize())
							.setHint(SqlNamingConvention.Graph.PROPERTY_GRAPH, entityGraph).getResultList()
					: entityManager.createQuery(query).setHint(SqlNamingConvention.Graph.PROPERTY_GRAPH, entityGraph)
							.getResultList();
			return new PageImpl<>(content, pageable, total);
		}
		Specification<T> specifications = searchFilters.toSpecifications();
		query.where(specifications.toPredicate(root, query, builder));
		countQuery.where(specifications.toPredicate(countRoot, query, builder));
		countQuery.select(builder.count(countRoot));
		long total = entityManager.createQuery(countQuery).getSingleResult();
		List<T> content = (pageable != null)
				? entityManager.createQuery(query).setFirstResult((int) pageable.getOffset())
						.setMaxResults(pageable.getPageSize())
						.setHint(SqlNamingConvention.Graph.PROPERTY_GRAPH, entityGraph).getResultList()
				: entityManager.createQuery(query).setHint(SqlNamingConvention.Graph.PROPERTY_GRAPH, entityGraph)
						.getResultList();
		return new PageImpl<>(content, pageable != null ? pageable : PageRequest.of(0, content.size() + 1), total);
	}

	@Override
	public T findOneByFilters(SearchFilters<T> searchFilters) {
		Pageable pageable = PageRequest.of(0, 1);
		Page<T> page = findByFilters(searchFilters, pageable);
		if (page.hasContent())
			return page.getContent().get(0);
		return null;
	}

	@Override
	public Page<T> findByFilters(String searchFilters, Pageable pageable) {
		return findByFilters(SearchFilters.fromString(searchFilters), pageable);
	}

	@Override
	public T findOneByFilters(String searchFilters) {
		return findOneByFilters(SearchFilters.fromString(searchFilters));
	}
}
