package ru.practicum.shareit.item;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.item.model.Item;

public class ItemSpecifications {

    public static Specification<Item> nameOrDescriptionContainsAndAvailableTrue(
            String name, String description) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%")
                    ),
                    criteriaBuilder.isTrue(root.get("available"))
            );
        };
    }
}