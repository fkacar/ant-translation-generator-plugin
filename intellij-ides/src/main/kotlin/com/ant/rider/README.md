# JetBrains Rider Support

The classes in this folder contain the features required for the plugin to work in JetBrains Rider.

## Structural Classes

### ComplementaryRiderPlugin

This class is the main entry point for Rider-related functions. It provides the following features:

- Rider environment initialization detectors
- Detection of translation files in C# projects
- Project service integration

### RiderStartupActivity 

This class runs automatically when Rider starts and activates the Rider features of the plugin.

### CSharpTranslationUtils

This is a helper class used to detect translation files in C# projects.
It includes the following features:

- Finding common translation directories
- Scanning for translation files across the project
- Identifying translation files based on name and extension

### RiderActionHandler, GenerateRiderTranslationHandler, RemoveRiderTranslationHandler

These classes provide Rider-specific event management:

- Creating translations for selected text in C# projects
- Removing translation keys
- Rider-specific shortcut and processing logic

## Installation and Usage

Rider support is automatically activated. To create translation keys in C# projects:

1. Select the text
2. Choose "Generate C# Translation Key" from the context menu or use the "Alt+R T" shortcut

## Translation Files

The following translation file formats are supported:

- JSON files
- RESX resources
- XML-based translation files

## C# Project Detection

The plugin scans for the following common translation directories in C# projects:

- Resources/Localization
- Resources/Translations 
- Resources/i18n
- Localization
- Translations
- i18n 