# Genesis Templates

# Overview

A genesis template is used to generate a set of files.

When a template is executed, a set of variables can be optionally provided to the template engine.

The template engine will then run through all the steps that are defined in the template.

For example a commonly used step for example is "input", which will request input from the user and set the provided response in a variable.

After all step are processed, it will go through each file, process it as a [freemarker](https://freemarker.apache.org/) 
template, and then use the that [freemarker](https://freemarker.apache.org/) template combined with all variables and write the 
resulting file content.

# Template file

The basic template will consist of a json or yaml file.

The top level JSON object can contain the following (optional) high level attributes are supported:

| attribute name | type | optional | value |
|----------------|------|------|----|
| title | string | Yes | A short title describing the template's purpose|
| description | string | Yes | A more extended description
| files | array | Yes | files to generate
| steps | array | Yes | steps to run through before generating the template

ie:

```json
{
  "title": "Basic maven template",
  "description": "This template is used to create a basic maven project"
  "files":  [],
  "steps": []
}
```

# Files

Each file object supports the following attributes

| attribute name | type | optional | value |
|----------------|----------|------|-------|
| path | string | No | File path
| process | boolean | Yes |  if the file's content should be processed through the freemarker template engine
| content | string | Yes | what encoding should be used when writing the file's content
| encoding | string | Yes | File's content
| resource | string | Yes | Path to a resource containing the file's content
| skip | string | Yes | If this is set to 'true' it will skip writing this file's content

# Archive template

Rather than having a single file template that contains all the file contents, you can instead create a directory, a 
zip or a jar archive containing all the file's content.

Such an archive must have the template json (or yaml) in a file named 'genesis-template.json' or 'genesis-template.yml'

Then all the template files should be stored under the path 'files'

Those files can then be referred to by the json file object using the 'resource' attribute.

Any file with the same path as a file in the descriptor which doesn't set it's content will be automatically 
associated with the file as if the resource attribute was set to the same path.

Any file that isn't referred to from the template file will be automatically added to the list of files.

# Example

Let's say there's an archive or directory as described below, if you ran the template and provided 'john' as the answer
to the "what is your name?" you would get the result as described

## Template files

### genesis-template.json

```json
{
  "steps": [
    { "type": "input", "var": "${myname}", "message": "What is your name?" }
  ],
  "files": [
    { "path": "hello.txt", "resource": "world.txt" },
    { "path": "somedir/foo.txt" },
  ]
}
```

### files/world.txt

```
hi ${myname}
```

### files/somedir/foo.txt

```
bar
```

### files/vehicle/plane.txt

```
bird
```

## Resulting files

### hello.txt

```
hi john
```

### somedir/foo.txt

```
bar
```

### vehicle/plane.txt

```
bird
```
