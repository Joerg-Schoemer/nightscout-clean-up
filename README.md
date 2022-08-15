# Nighscout clean-up

This small spring-boot application deletes the duplicates within the nightscout mongodb.
Those duplicates were produced when running the dev branch of LoopKit for a while.
It is fixed since this [Loop commit](https://github.com/LoopKit/Loop/commit/01e8aa3573c4dd309f60e8f64ff407cdd4317e21)

## Detecting duplicates

The duplicates will be detected by the following rules

1) the creation date of two consecutive status records is less than 120 seconds
2) the predicted startDate of both records are equal
3) the euclidean distance of the predicted vectors is less than `--cleanup.distance` which defaults to 10

## Backup mongodb

Make a backup of your mongodb with

```shell
mongodump --archive=<database-name>.backup.gz --gzip --db=<database-name> --uri="mongodb+srv://<username>:<password>@<hostname>/?retryWrites=true&w=majority"
```

replace all values marked with the sharp brackets `<>` with your mongodb settings.

| name          | description                             |
|---------------|-----------------------------------------|
| username      | the username to connect to your mongodb |
| password      | the password to connect to your mongodb |
| hostname      | the hostname of your mongodb            |
| database-name | your database name of your mongodb      |

e.g. 
* username = otto
* password = top-secret-password
* database-name = nightscout
* hostname = cluster0.a5dcj.mongodb.net

````shell
mongodump --archive=nightscout.backup.gz --gzip --db=nightscout --uri="mongodb+srv://otto:top-secret-password@cluster0.a5dcj.mongodb.net/?retryWrites=true&w=majority"
````

If you don't have a `mongodump` you can easily install it with

```shell
brew tap mongodb/brew
brew install mongodb-database-tools
```

And if you even do not know what to brew here (https://brew.sh) you can read about it. 

## Running clean-up

To run the application ensure you have an openjdk 11 installed 
e.g.:
```shell
brew install openjdk@11 
```
### Command line arguments

```
./gradlew bootRun --args="--help"

Usage: nightscout-clean-up [Options] [Commands]

Options:

        --dry-run: do not delete duplicates just show a number of records defined by --num 
                   or writes the duplicates to file with --out

        --num: number of records to show: defaults to 10

        --out: specifies a filename to store the duplicates as json.
               If the filename ends with .gz it will be gzipped.

        --cleanup.distance: the threshold to treat vectors as equal. 
                            If the euclidean distance is less than the threshold
                            the vector is considered identical: defaults to 10

Commands:

        --help: show this screen
````

### Check

First of all you should check if you've duplicates.

1. Start a Terminal (e.g. `iterm2` or `Terminal`)
1. Clone this git repository
1. Jump into the cloned git repository
1. Make a dry-run to see how many records will be deleted 
   ```shell
   ./gradlew bootRun --args="--dry-run --out=duplicates.json --spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<hostname>/?retryWrites=true&w=majority --spring.data.mongodb.database=<batabase-name>"
   ```
   Replace the values of the "sharp brackets" again.
   The statement produces a file `duplicates.json` with the duplicates.
1. Maybe have a look into the `duplicates.json`
1. Play around with `--cleanup.distance` to catch more or less duplicates.

### Delete

When you're sure you want to delete those duplicates with your estimated `-cleanup.distance` setting run the command without `--dry-run` and `--out` but with your `--cleanup.distance`:

```shell
./gradlew bootRun --args="--spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<hostname>/?retryWrites=true&w=majority --spring.data.mongodb.database=<batabase-name> [--cleanup.distance=<distance>]"
```
