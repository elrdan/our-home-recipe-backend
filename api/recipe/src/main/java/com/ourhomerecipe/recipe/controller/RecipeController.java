package com.ourhomerecipe.recipe.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.recipe.service.RecipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {
	private final RecipeService recipeService;
}
