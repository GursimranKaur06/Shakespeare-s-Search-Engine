# Shakespeare's Search Engine

This project is an Information Retrieval system (a.k.a. Search Engine) implementing an inverted index on the dataset of Shakespeare’s plays. It is structured into the following modules :

## MODULE 1 : Inverted Index
This system essentially forms an inverted index from a corpus of documents, i.e., scenes from the complete works of Shakespeare and then later used to perform query processing. The dataset has been preprocessed by tokenizing the terms, stripped of punctuation and stemmed using the Krovetz Stemmer. The data is first read from the json file and divided into json objects of 1 scene each. These scenes are then traversed one at a time and the text from each scene is then tokenized into words to form the key of the Inverted Index. The data of each of these words, i.e., document id, position, count, etc. is then recorded into objects of Postings and the array of these objects is stored in PostingsList object along with other relevant data. This object forms the value of the map. This map is then used to delta encode and then vbyte encode the data to write into a compressed file. There is also an option for writing the index to a file without the compression. These indices are then read onto the memory and a word from both is extracted and it’s postings list is printed to compare that the process of writing both the indices was accurate.
The next task of the system is to use the index to process the queries. First, 100 sets of 7 random words are generated from the vocabulary. These sets are then processed as queries one by one using the document-at-a-time retrieval. The top k documents for each query are printed. Now, each set is traversed and the dice’s coefficient for each term in the set is calculated for the whole vocabulary. The word with the max dice coefficient is paired with the set term and written to a file. This file is then read again and the sets of 14 terms are taken input as queries and the document-at-a-time retrieval is performed again on these to give the top k documents as ranked. The system also provides some extra information like the average length of the scene, shortest scene, longest play and shortest play.

## MODULE 2 : Ranking using BM25
This module implements the BM25 ranking algorithm that ranks a set of documents based of the query terms appearing in those documents. Score is not calculated for documents in which none of the query terms appear. This algorithm traverses all the documents one by one and checks if the query words appear in that document. If yes, the BM25 score is calculated for the query term for that particular document. These scores are then summed to get the score for all terms in a query for a document. The top k scores are then returned. Top 10 results are retrieved for this implementation.
This phase also implemented 2 smoothing techniques to counter the zero probability problem. The first smoothing technique used is Jelinek Mercer which uses all the query words with repetitive values and calculates the score for that query per document. The second smoothing technique implemented is Dirichlet which takes the sample size into account. Both these return the top 10 relevant documents.

## MODULE 3 : Inference Network
This module implements structured queries. An Inference Network Model is used with evidence combination to process complex queries. The queries are split into Term Nodes that represent the terms along with their postings lists. The operators such as AND, OR, NOT, etc. are implemented as belief nodes. The concept of ‘window’ is implemented as ordered and unordered windows. The ordered window takes into account the order of the terms in the queries whereas the unordered window can shuffle the terms within the window of words. The code carries out the AND, MAX, OR, SUM operators on 10 queries along with the ordered window and unordered window applications. The results are printed in the standard Trecrun format.

## MODULE 4 : Clustering
This phase implements agglomerative clustering with four linking choices, namely, Single, Complete, Average and Mean. The Single strategy matches a doc with a doc in the cluster that has the most similarity with it, producing spread out clusters. The Complete linking strategy matches a doc with a doc in the cluster with the least similarity and hence produces compact clusters. Average produces an averaged score across the documents of the cluster. Mean calculates similarity of the document with the centroid of the cluster. These strategies are implemented for threshold values from 0.05 to 0.95 with a difference of 0.05.

## MODULE 5 : Query Independent Features
This module implements query independent features. A prior is a document dependent feature, represented by a value that can be interpreted as a probability. For this project 2 prior probabilities are created, one is uniformly distributed and the other is randomly distributed. A query is evaluated using the uniform prior once and the random prior the second time. Belief AND is used as the evidence combination method. All results are saved in trecrun format.
