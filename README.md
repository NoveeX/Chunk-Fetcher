# Chunk-Fetcher
A fabric mod to fetch and analyze chunk data within render distance.

### • Usage:
```
/chunkfetcher <detailed: boolean> <radius: int>
```
#### Parameters:
| Parameter   | Type       | Description                                                                 |
| :---------- | :--------- | :-------------------------------------------------------------------------- |
| detailed  | `boolean`  | Tells the analyzer to perform a more in-depth search. May take longer to finish, but provides more information. |
| radius    | `int`      | Specifies how many chunks in each direction to search. |

#### **The output of the search will be found in:**
`*game-directory*/ChunkFetcherData_yyyy-MM-dd-HH-mm.json`
